package org.globus.cs.render.rest;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.api.json.JSONWithPadding;
import org.globus.cs.render.impl.Page;
import org.globus.cs.render.impl.PageStore;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/pages/{name}")
@RequestScoped
public class PageResource {
    @PathParam("name")
    private String name;
    @Context
    private Request request;
    @Context
    private HttpServletRequest servletRequest;
    @Context
    private UriInfo info;
    private PageStore store;


    @Inject
    public PageResource(PageStore store) {
        this.store = store;
    }

    @GET
    @Produces({"application/javascript", "application/org.globus.cs.webdef.page+json", "application/x-javascript",
            "text/ecmascript", "application/ecmascript", "text/jscript"})
    public Response getPage(@QueryParam("callback") String callback) throws Exception{
        Page page = store.getPage(name);
        //Check version
        Response.ResponseBuilder rb = request.evaluatePreconditions(new EntityTag(page.version));
        if (rb != null)
            return rb.build();
        JsonpResult result;
        if (callback == null) {
            result = new JsonpResult(page);
        } else {
            result = new JsonpResult(page, callback);
        }
        String returnType = servletRequest.getHeader("Accepts");
        if(MediaType.WILDCARD.equals(returnType)){
            returnType = "application/javascript";
        }        
        return Response.ok(result).type(returnType).tag(page.version).build();
    }


    @POST
    @Consumes("application/org.globus.cs.webdef.page+json")
    public Response storePage(Page page){
        try {
            store.storePage(name,page);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.fromStatusCode(500));
        }
        return Response.created(info.getAbsolutePath()).build();
    }
}
