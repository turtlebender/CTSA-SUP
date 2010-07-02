package org.globus.cs.render.rest;

/**
 * Created by IntelliJ IDEA.
 * User: trhowe
 * Date: Jul 1, 2010
 * Time: 5:56:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class JsonpResult {
    public String callback;
    public Object resource;

    public JsonpResult() {
    }

    public JsonpResult(Object resource) {
        this.resource = resource;
    }

    public JsonpResult(Object resource, String callback) {
        this.callback = callback;
        this.resource = resource;
    }
}
