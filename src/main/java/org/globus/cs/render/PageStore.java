package org.globus.cs.render;

import org.globus.cs.render.impl.Page;

/**
 * Created by IntelliJ IDEA.
 * User: trhowe
 * Date: Jul 1, 2010
 * Time: 2:04:09 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PageStore {
    Page getPage(String name) throws Exception;

    void storePage(String name, Page page) throws Exception;
}
