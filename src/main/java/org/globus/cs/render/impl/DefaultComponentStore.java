package org.globus.cs.render.impl;

import com.google.inject.Inject;
import org.globus.cs.render.ComponentStore;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;


public class DefaultComponentStore implements ComponentStore {
    private ComponentStore remoteStore;
    private ComponentStore localStore;

    @Inject
    public DefaultComponentStore(@Local ComponentStore localStore, @Remote ComponentStore remoteStore){
        this.localStore = localStore;
        this.remoteStore = remoteStore;
    }

    public Component getComponent(UriBuilder baseBuilder, UriBuilder relativeBuilder, String name) throws Exception {
        Component component = localStore.getComponent(baseBuilder, relativeBuilder, name);
        if(component == null){
            component = remoteStore.getComponent(baseBuilder, relativeBuilder, name);
        }
        return component;
    }

    public void storeComponent(String name, Component component) throws Exception {
        this.localStore.storeComponent(name, component);
    }
}
