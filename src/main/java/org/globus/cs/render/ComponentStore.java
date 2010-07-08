package org.globus.cs.render;

import org.globus.cs.render.impl.Component;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: trhowe
 * Date: Jul 1, 2010
 * Time: 10:47:28 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ComponentStore {
    Component getComponent(UriBuilder baseBuilder, UriBuilder relativeBuilder, String name) throws Exception;

    void storeComponent(String name, Component component) throws Exception;
}
