package org.globus.cs.render.impl;

import com.sun.jersey.api.client.Client;
import org.globus.cs.render.RemoteResourceHelperFactory;


public class HashMapRemoteResourceHelperFactory implements RemoteResourceHelperFactory {

    public <T> RemoteResourceHelper<T> createResourceHelper(Client client, Class<T> resourceType) {
        return new RemoteResourceHelper<T>(client, resourceType);
    }
}
