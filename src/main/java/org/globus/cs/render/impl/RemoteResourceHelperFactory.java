package org.globus.cs.render.impl;

import com.sun.jersey.api.client.Client;


public interface RemoteResourceHelperFactory {

     <T> RemoteResourceHelper<T> createResourceHelper(Client client, Class<T> resourceType);
}
