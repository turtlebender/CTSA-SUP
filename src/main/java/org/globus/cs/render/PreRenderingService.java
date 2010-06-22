package org.globus.cs.render;

import java.io.Writer;
import java.net.URI;

/**
 * This interface takes a template uri and slot definitions and renders the template to DHTML.
 *
 * @author Tom Howe
 */
public interface PreRenderingService {

    /**
     * Render a template and slot mapping document to HTML on the supplied OutputStream.
     *
     * @param templateLocation A URI which specified which template should be used for rendering.
     * @param slotMapping A set of Mappings which specify what components should be placed in which slot.
     * @param writer An output stream to write the pre-rendered object
     * @throws RenderingException If there is an error rendering the template.
     */
    void prerender(URI templateLocation, URI slotMapping, Writer writer) throws RenderingException;

}
