package org.globus.cs.render.impl;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: trhowe
 * Date: Jul 1, 2010
 * Time: 8:49:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class Content<T> implements Serializable {
    public String uri;
    public T content;
    public String version;

    public Content(String uri, T content, String version) {
        this.uri = uri;
        this.content = content;
        this.version = version;
    }

    public Content() {
    }

    public Content(T content, String version) {
        this.content = content;
        this.version = version;
    }

    public Content(T content) {
        this.content = content;
    }
}
