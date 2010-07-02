package org.globus.cs.render.rest;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import org.globus.cs.render.impl.Component;
import org.globus.cs.render.impl.ComponentStore;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/components/{name}")
@RequestScoped
public class ComponentResource {
    @Context
    private UriInfo info;
    private ComponentStore componentStore;

    @Inject
    public ComponentResource(ComponentStore store){
        this.componentStore = store;
    }


    @GET
    @Produces("application/org.globus.cs.webdef.component+json")
    public Component getComponent() throws Exception {
        return componentStore.getComponent(info.getAbsolutePath().toASCIIString());
    }

    @POST
    @Consumes("application/org.globus.cs.webdef.component+json")
    public Response storeComponent(Component component) throws Exception{
        componentStore.storeComponent(info.getAbsolutePath().toASCIIString(), component);
        return Response.created(info.getAbsolutePath()).build();
    }
}
