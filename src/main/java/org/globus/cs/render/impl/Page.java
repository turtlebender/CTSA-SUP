package org.globus.cs.render.impl;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Page {
    public String templateURI;
    public String slotdefURI;

    public Page() {
    }

    public Page(String templateURI, String slotdefURI) {
        this.templateURI = templateURI;
        this.slotdefURI = slotdefURI;
    }

}
