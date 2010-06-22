package org.globus.cs.render;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: trhowe
 * Date: Jun 21, 2010
 * Time: 7:32:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TemplateResolver {

    InputStream resolveTemplate(URI uri) throws IOException;
}
