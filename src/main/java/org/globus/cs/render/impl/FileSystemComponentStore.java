package org.globus.cs.render.impl;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.globus.cs.render.ComponentStore;
import org.globus.cs.render.RemoteResourceHelperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class FileSystemComponentStore implements ComponentStore {
    private final File STORAGE_DIR;
    private final File COMPONENT_STORAGE_DIR;
    private ObjectMapper mapper = new ObjectMapper();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private RemoteResourceHelper<String> resourceHelper;


    @Inject
    public FileSystemComponentStore(@RealComponentPath File componentPath, @RealComponentPath File realPath, Client client, RemoteResourceHelperFactory resourceHelperFactory) throws Exception {
        STORAGE_DIR = realPath;
        COMPONENT_STORAGE_DIR = componentPath;
        resourceHelper = resourceHelperFactory.createResourceHelper(client, String.class);
        if (!STORAGE_DIR.exists()) {
            if (!STORAGE_DIR.mkdirs()) {
                logger.warn("Unable to create component store storage directory: {}", STORAGE_DIR);
            }
        }
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
        mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
    }

    public Component getComponent(UriBuilder baseBuilder, UriBuilder relativeBuilder, String name) throws Exception {
        logger.debug("Reading component: {}", name);
        File componentFile = new File(COMPONENT_STORAGE_DIR, name);
        logger.debug("Reading file: {}", componentFile.toURI().toASCIIString());

        if (componentFile.exists()) {
            FileReader reader = new FileReader(componentFile);
            Component component = mapper.readValue(reader, Component.class);
            String[] path = name.split("/");
            StringBuilder builder = new StringBuilder();
            for(int i = 0 ; i < path.length - 1 ; i++){
                builder.append(path[i]);
            }
            Content content = component.content;
            if (content.uri != null) {
                String tmpUri = component.content.uri;
                if (!tmpUri.startsWith("http")) {
                    if (!tmpUri.startsWith("/")) {
                        tmpUri = relativeBuilder.path(builder.toString()).path(content.uri).build().toASCIIString();
                    } else {
                        tmpUri = baseBuilder.path(content.uri).build().toASCIIString();
                    }
                }
                Versioned<String> versioned = resourceHelper.getComponent(tmpUri);
                component.content.content = versioned.resource;
                component.content.version = versioned.version.getValue();
            }
            return component;
        }
        return null;
    }

    public void storeComponent(String name, Component component) throws Exception {
        logger.debug("Writing component: {}", name);
        component.version = UUID.randomUUID().toString();
        File componentFile = new File(STORAGE_DIR, name);
        File parent = componentFile.getParentFile();
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                logger.info("Unable to create path: " + name);
                throw new IllegalArgumentException("Unable to create path: " + name);
            }
        }
        logger.debug("Writing file: {}", componentFile.toURI().toASCIIString());
        FileWriter writer = new FileWriter(componentFile);
        mapper.writeValue(writer, component);
        writer.close();
    }    
}
