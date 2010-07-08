package org.globus.cs.render.impl;

import java.io.Serializable;

public class ProxyResponse implements Serializable {

    public String content;

    public String responseCode;

    public String uri;

    public ProxyResponse() {
    }

    public ProxyResponse(String content, String responseCode, String uri) {
        this.content = content;
        this.responseCode = responseCode;
        this.uri = uri;
    }
}
