package org.globus.cs.render.impl;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import org.globus.cs.render.ComponentStore;
import org.globus.cs.render.RemoteResourceHelperFactory;

public class RemoteComponentStore implements ComponentStore {
    private RemoteResourceHelper<Component> resourceHelper;

    @Inject
    public RemoteComponentStore(Client client, RemoteResourceHelperFactory factory){
        this.resourceHelper = factory.createResourceHelper(client, Component.class);
    }

    public Component getComponent(String name) throws Exception {
        return resourceHelper.getComponent(name).resource;
    }
    
    public void storeComponent(String name, Component component) throws Exception {
        throw new UnsupportedOperationException();
    }
}
