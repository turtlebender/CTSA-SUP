package org.globus.cs.render.impl;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.EntityTag;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class RemoteResourceHelper<T> {
    Client client;
    private Map<String, Versioned<T>> resourceCache = new HashMap<String, Versioned<T>>();
    private Class<T> type;

    public RemoteResourceHelper(Client client, Class<T> type) {
        this.client = client;
        this.type = type;
    }

    public Versioned<T> getComponent(String name) throws Exception {
        Versioned<T> versioned = resourceCache.get(name);
        ClientResponse response;
        WebResource resource = client.resource(name);
        T component;
        if (versioned != null) {
            response = resource.header("If-None-Match", versioned.version.getValue()).get(ClientResponse.class);
            if (response.getStatus() == ClientResponse.Status.NOT_MODIFIED.getStatusCode()) {
                return versioned;
            }
        } else {
            response = resource.get(ClientResponse.class);
        }
        component = response.getEntity(type);
        //TODO: check cache control
        EntityTag version = response.getEntityTag();
        if(version == null){
            version = new EntityTag(UUID.randomUUID().toString());
        }
        versioned = new Versioned<T>(component, version);
        resourceCache.put(name, versioned);
        return versioned;
    }
}
