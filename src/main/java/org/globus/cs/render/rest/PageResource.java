package org.globus.cs.render.rest;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import org.globus.cs.render.PageStore;
import org.globus.cs.render.impl.Page;
import org.globus.cs.render.impl.RealPagePath;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/{name:.*.pagedesc}")
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
    @Produces("application/org.globus.cs.webdef.page+json")
    public Response getJSONPage() throws Exception{
        return getPage(null);
    }

    @GET
    @Produces({"application/javascript", "application/x-javascript", "text/ecmascript", "application/ecmascript",
            "text/jscript"})
    public Response getPage(@QueryParam("callback") String callback) throws Exception {
        URI parent = getParent(info.getAbsolutePath());
        UriBuilder parentBuilder = UriBuilder.fromUri(parent);
        Page page = store.getPage(info.getBaseUriBuilder(), parentBuilder, name);
        //Check version
        if (page.version != null) {
            Response.ResponseBuilder rb = request.evaluatePreconditions(new EntityTag(page.version));
            if (rb != null)
                return rb.build();
        }
        JsonpResult result;
        if (callback == null) {
            result = new JsonpResult(page);
        } else {
            result = new JsonpResult(page, callback);
        }
        String returnType = servletRequest.getHeader("Accepts");
        if (MediaType.WILDCARD.equals(returnType)) {
            returnType = "application/javascript";
        }
        return Response.ok(result).type(returnType).tag(page.version).build();
    }


    @POST
    @Consumes("application/org.globus.cs.webdef.page+json")
    public Response storePage(Page page) {
        try {
            store.storePage(name, page);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.fromStatusCode(500));
        }
        return Response.created(info.getAbsolutePath()).build();
    }

    private URI getParent(URI uri) throws URISyntaxException {

        String parentPath = new File(uri.getPath()).getParent();

        if (parentPath == null) {
            return new URI("../");
        }

        return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), parentPath.replace('\\', '/'), uri.getQuery(), uri.getFragment());
    }
}

