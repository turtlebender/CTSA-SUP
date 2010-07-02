package org.globus.cs.render.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Page implements Serializable {
    public String name;
    public String version;
    public String author;
    public String description;
    public Map<String, Component> slotMappings = new HashMap<String, Component>();
    public Map<String, Preload> preload = new HashMap<String, Preload>();

    public Page() {
    }

    public Page(String name, String version, String author, Map<String, Component> slotMappings) {
        this.name = name;
        this.version = version;
        this.author = author;
        this.slotMappings = slotMappings;
    }
}
