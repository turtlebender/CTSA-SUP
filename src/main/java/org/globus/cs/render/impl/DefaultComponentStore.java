package org.globus.cs.render.impl;

import com.google.inject.Inject;
import org.globus.cs.render.ComponentStore;


public class DefaultComponentStore implements ComponentStore {
    private ComponentStore remoteStore;
    private ComponentStore localStore;

    @Inject
    public DefaultComponentStore(@Local ComponentStore localStore, @Remote ComponentStore remoteStore){
        this.localStore = localStore;
        this.remoteStore = remoteStore;
    }

    public Component getComponent(String name) throws Exception {
        Component component = localStore.getComponent(name);
        if(component == null){
            component = remoteStore.getComponent(name);
        }
        return component;
    }

    public void storeComponent(String name, Component component) throws Exception {
        this.localStore.storeComponent(name, component);
    }
}
