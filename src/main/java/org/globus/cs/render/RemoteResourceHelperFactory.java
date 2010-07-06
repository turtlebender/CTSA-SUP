package org.globus.cs.render;

import com.sun.jersey.api.client.Client;
import org.globus.cs.render.impl.RemoteResourceHelper;


public interface RemoteResourceHelperFactory {

     <T> RemoteResourceHelper<T> createResourceHelper(Client client, Class<T> resourceType);
}
