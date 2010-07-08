package org.globus.cs.render.impl;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import org.globus.cs.render.ComponentStore;
import org.globus.cs.render.RemoteResourceHelperFactory;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class RemoteComponentStore implements ComponentStore {
    private RemoteResourceHelper<Component> resourceHelper;

    @Inject
    public RemoteComponentStore(Client client, RemoteResourceHelperFactory factory){
        this.resourceHelper = factory.createResourceHelper(client, Component.class);
    }

    public Component getComponent(UriBuilder baseBuilder, UriBuilder relativeBuilder, String name) throws Exception {
        return resourceHelper.getComponent(name).resource;
    }
    
    public void storeComponent(String name, Component component) throws Exception {
        throw new UnsupportedOperationException();
    }
}
