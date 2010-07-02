package org.globus.cs.render.impl;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "webRegistry")
public class WebRegistry {
    private String[] pageDefinitions;
    private String[] components;

    public WebRegistry(String[] pageDefinitions, String[] components) {
        this.pageDefinitions = pageDefinitions;
        this.components = components;
    }

    public WebRegistry() {

    }

    public String[] getPageDefinitions() {
        return pageDefinitions;
    }

    public void setPageDefinitions(String[] pageDefinitions) {
        this.pageDefinitions = pageDefinitions;
    }

    public String[] getComponents() {
        return components;
    }

    public void setComponents(String[] components) {
        this.components = components;
    }
}
