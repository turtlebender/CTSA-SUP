package org.globus.cs.render.impl;

import javax.ws.rs.core.EntityTag;


public class Versioned<T> {

    public T resource;
    public EntityTag version;

    public Versioned() {
    }

    public Versioned(T resource, EntityTag version) {
        this.resource = resource;
        this.version = version;
    }
}
