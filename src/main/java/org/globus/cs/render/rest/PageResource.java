package org.globus.cs.render.rest;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import org.globus.cs.render.PreRenderingService;
import org.globus.cs.render.impl.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: trhowe
 * Date: Jun 23, 2010
 * Time: 1:28:12 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/pages")
@RequestScoped
public class PageResource {
    private PreRenderingService renderer;
    private static Map<String, Page> pages = new HashMap<String, Page>();
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    public PageResource(PreRenderingService renderer) {
        this.renderer = renderer;
    }

    @Path("/{name}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response writePage(@PathParam("name") String name) throws Exception {
        logger.info("Getting page {}", name);
        Page page = pages.get(name);
        if (page == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(os);
        renderer.prerender(new URI(page.templateURI), new URI(page.slotdefURI), writer);
        writer.close();
        return Response.ok(os.toByteArray()).build();
    }

    @Path("/{name}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPage(@Context UriInfo uriInfo, @PathParam("name") String name, Page pageDef) {
        logger.info("storing page {}", name);
        pages.put(name, pageDef);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(name).build()).build();
    }
}
