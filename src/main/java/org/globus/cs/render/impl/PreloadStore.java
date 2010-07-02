package org.globus.cs.render.impl;

/**
 * Created by IntelliJ IDEA.
 * User: trhowe
 * Date: Jul 1, 2010
 * Time: 10:45:29 AM
 * To change this template use File | Settings | File Templates.
 */
public interface PreloadStore {
    Preload getPreload(String uri) throws Exception;
}
