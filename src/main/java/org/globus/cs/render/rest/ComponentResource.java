package org.globus.cs.render.rest;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import org.globus.cs.render.ComponentStore;
import org.globus.cs.render.impl.Component;
import org.globus.cs.render.impl.RealComponentPath;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/components")
@RequestScoped
public class ComponentResource {
    @Context
    private UriInfo info;

    private ComponentStore componentStore;
    private File componentPath;

    @Inject
    public ComponentResource(@RealComponentPath File componentPath, ComponentStore store) {
        this.componentStore = store;
        this.componentPath = componentPath;
    }


    @GET
    @Path("{name:.*.desc}")
    @Produces("application/org.globus.cs.webdef.component+json")
    public Component getComponent(@PathParam("name") String name) throws Exception {
        UriBuilder parentBuilder = UriBuilder.fromUri(getParent(info.getAbsolutePath()));
        return componentStore.getComponent(info.getBaseUriBuilder(), parentBuilder, name);
    }

    @POST
    @Path("{name:.*.desc}")
    @Consumes("application/org.globus.cs.webdef.component+json")
    public Response storeComponent(@PathParam("name") String name, Component component) throws Exception {
        componentStore.storeComponent(name, component);
        return Response.created(info.getAbsolutePath()).build();
    }

    @GET
    @Path("{name:.*.frag}")
    public Response getNonComponent(@PathParam("name") String name) throws Exception{
        File file = new File(componentPath, "components");
        file = new File(file, name);
        if(file.exists()){
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String s;
            while((s = br.readLine()) != null) {
                builder.append(s);
            }
            br.close();
            reader.close();
            return Response.ok(builder.toString()).build();
        }
        return Response.noContent().build();
    }

    private URI getParent(URI uri) throws URISyntaxException {

        String parentPath = new File(uri.getPath()).getParent();

        if (parentPath == null) {
            return new URI("../");
        }

        return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), parentPath.replace('\\', '/'), uri.getQuery(), uri.getFragment());
    }
}
