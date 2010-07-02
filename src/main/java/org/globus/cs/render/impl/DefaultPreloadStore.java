package org.globus.cs.render.impl;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;


public class DefaultPreloadStore implements PreloadStore {
    private RemoteResourceHelper<String> resourceHelper;

    @Inject
    public DefaultPreloadStore(Client client, RemoteResourceHelperFactory resourceHelperFactory){
        resourceHelper = resourceHelperFactory.createResourceHelper(client, String.class);
    }

    public Preload getPreload(String uri) throws Exception {
        Preload preload = new Preload();
        preload.uri = uri;
        preload.content = resourceHelper.getComponent(uri).resource;
        return preload;
    }
}
