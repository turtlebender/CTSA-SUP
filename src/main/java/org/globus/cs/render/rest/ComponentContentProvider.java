package org.globus.cs.render.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Produces("text/org.globus.cs.webfrag+html")
@Consumes("text/org.globus.cs.webfrag+html")
public class ComponentContentProvider implements MessageBodyWriter<String>, MessageBodyReader<String> {

    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return String.class.equals(aClass) && mediaType.equals(MediaType.valueOf("text/org.globus.cs.webfrag+html"));
    }

    public long getSize(String s, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return s.getBytes().length;

    }

    public void writeTo(String s, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> stringObjectMultivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        byte[] bytes = s.getBytes();
        outputStream.write(bytes, 0, bytes.length);
    }

    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return String.class.equals(aClass) && mediaType.equals(MediaType.valueOf("text/org.globus.cs.webfrag+html"));
    }

    public String readFrom(Class<String> stringClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        br.close();
        return sb.toString();
    }

}
