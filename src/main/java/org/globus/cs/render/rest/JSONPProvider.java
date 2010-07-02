package org.globus.cs.render.rest;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

@Provider
public class JSONPProvider implements MessageBodyReader<JsonpResult>, MessageBodyWriter<JsonpResult> {
    private ObjectMapper mapper = new ObjectMapper();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static Set<MediaType> validTypes = new HashSet<MediaType>();

    static {
        validTypes.add(MediaType.valueOf("application/javascript"));
        validTypes.add(MediaType.valueOf("text/ecmascript"));
        validTypes.add(MediaType.valueOf("application/x-javascript"));
        validTypes.add(MediaType.valueOf("application/ecmascript"));
        validTypes.add(MediaType.valueOf("text/jscript"));
    }

    public JSONPProvider() {
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
        mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
    }

    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return JsonpResult.class.equals(aClass) && JSONPProvider.validTypes.contains(mediaType);
    }

    public JsonpResult readFrom(Class<JsonpResult> objectClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        Reader reader = new InputStreamReader(inputStream);
        JsonpResult o = mapper.readValue(reader, objectClass);
        reader.close();
        return o;
    }

    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return JsonpResult.class.equals(aClass) && mediaType.equals(MediaType.valueOf("application/javascript"));
    }

    public long getSize(JsonpResult o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(JsonpResult o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> stringObjectMultivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        logger.debug("writing jsonP");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mapper.writeValue(baos, o.resource);
        PrintWriter writer = new PrintWriter(outputStream);
        String callback = o.callback == null ? "callback" : o.callback;
        writer.write("if(window.");
        writer.write(callback);
        writer.write("){");
        writer.write(callback);
        writer.write("(");
        writer.write(new String(baos.toByteArray()));
        writer.write(");}");
        writer.flush();
        writer.close();
    }
}
