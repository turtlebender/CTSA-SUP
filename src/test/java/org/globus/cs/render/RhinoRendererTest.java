package org.globus.cs.render;

import org.globus.cs.render.impl.RhinoRenderingService;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static junit.framework.Assert.assertEquals;

public class RhinoRendererTest {

    RhinoRenderingService service;

    @Before
    public void init() throws Exception {
        service = new RhinoRenderingService();
        service.init();
    }

    /**
     * Just check to make sure that the pre-rendered text is equal to the expected output.
     *
     * @throws Exception if there is a failure in processing the template
     */
    @Test
    public void testRender() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        service.prerender(getClass().getResource("/TestHtml.html").toURI(),
                getClass().getResource("/slots.js").toURI(), writer);
        writer.close();
        String result = baos.toString();
        assertEquals("HTML Output does not match", EXPECTED_OUTPUT.replaceAll("\\s+", ""),
                result.replaceAll("\\s+", ""));
    }

    private static final String EXPECTED_OUTPUT = "<html>\n" +
            "<head>\n" +
            "    <title>Renderer Test</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<div id=\"slot1\"><p>slot1</p></div>\n" +
            "\n" +
            "<div id=\"slot2\"><p>slot2</p></div>\n" +
            "\n" +
            "<div id=\"slot3\"><p>slot3</p></div> \n" +
            "</body>\n" +
            "</html>";
}
