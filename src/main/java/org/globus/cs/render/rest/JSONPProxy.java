package org.globus.cs.render.rest;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.globus.cs.render.impl.ProxyResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/proxy")
@RequestScoped
public class JSONPProxy {
    private Client client;

    @Inject
    public JSONPProxy(Client client){
        this.client = client;
    }

    @QueryParam("url")
    String url;

    @QueryParam("contentType")
    String contentType;

    @GET
    public ProxyResponse proxyRequest(){
        WebResource resource = client.resource(url);
        ClientResponse response;
        if(contentType != null){
            response = resource.accept(contentType).get(ClientResponse.class);
        }else{
            response = resource.get(ClientResponse.class);
        }
        return new ProxyResponse(response.getEntity(String.class), Integer.toString(response.getStatus()), url);
    }
}
