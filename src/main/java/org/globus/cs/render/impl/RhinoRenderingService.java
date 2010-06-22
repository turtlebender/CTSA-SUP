package org.globus.cs.render.impl;

import org.globus.cs.render.PreRenderingService;
import org.globus.cs.render.RenderingException;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Global;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URI;

/**
 * This is an implementation of the PreRenderingService which use the Rhino engine to resolve the DHTML
 * into displayable HTML.  Specifically, this calls the "load" method of the supplied DHTML
 */
public class RhinoRenderingService implements PreRenderingService {

    Context context;
    ScriptableObject baseScope;
    final String ENV_JS = "/env.rhino.1.2.js";
    final String PRERENDER_SCRIPT = "/prerender.js";
    final String SLOT_LOCATION_VAR_NAME = "slotDefinitions";
    final String BASE_CONTEXT_NAME = "baseContext";
    final String RENDER_CONTEXT_NAME = "prerender";


    /**
     * Initialize the global Rhino context by loading global and env.js.
     *
     * @throws RenderingException If the initialization fails.
     */
    public void init() throws RenderingException {
        context = ContextFactory.getGlobal().enterContext();
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_1_7);
        Global global = new Global();
        baseScope = context.initStandardObjects();
        baseScope.setPrototype(global);
        global.init(context);
        try {
            context.evaluateReader(baseScope, new InputStreamReader(getClass().getResourceAsStream(ENV_JS)),
                    BASE_CONTEXT_NAME, 0, null);
        } catch (IOException ioe) {
            throw new RenderingException("Unable to load the envjs script", ioe);
        }
    }

    /**
     * Render the DHTML by injecting the specified slots into the template.  This uses the Rhino Javascript engine
     * to process the template
     *
     * @param templateLocation A URI which specified which template should be used for rendering.
     * @param slotMapping      A set of Mappings which specify what components should be placed in which slot.
     * @param writer           An output stream to write the pre-rendered object
     * @throws RenderingException
     */
    public void prerender(URI templateLocation, URI slotMapping, Writer writer) throws RenderingException {
        Context threadContext = ContextFactory.getGlobal().enterContext();
        try {
            Scriptable threadScope = getScope(threadContext);
            threadScope.put(SLOT_LOCATION_VAR_NAME, threadScope, slotMapping.toASCIIString().replace("file:/", "file:////"));
            Object candidate = threadScope.get("render", threadScope);
            if (!(candidate instanceof Function)) {
                throw new RenderingException("\"render\" is not a function");
            } else {
                Function renderFunction = (Function) candidate;
                Object result = renderFunction.call(threadContext, threadScope, threadScope, new Object[]{
                        templateLocation.toASCIIString().replace("file:/", "file:////"),
                        slotMapping.toASCIIString().replace("file:/", "file:////")}).toString();
                writer.write(result.toString());
            }
        } catch (IOException ioe) {
            throw new RenderingException(ioe);
        }
    }

    /**
     * Get a scope object which is pre-loaded with the rendering script.
     *
     * @param threadContext The context on which to build the scope.
     * @return The newly created scope.
     * @throws RenderingException If the scope cannot be prepared.
     */
    public Scriptable getScope(Context threadContext) throws RenderingException {
        Scriptable threadScope = threadContext.newObject(baseScope);
        threadScope.setPrototype(baseScope);
        threadScope.setParentScope(null);
        try {
            threadContext.evaluateReader(threadScope, new InputStreamReader(getClass().getResourceAsStream(PRERENDER_SCRIPT)),
                    RENDER_CONTEXT_NAME, 0, null);
        } catch (IOException ioe) {
            throw new RenderingException("Unable to prepare scope", ioe);
        }
        return threadScope;
    }


}
