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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;

public class FileSystemComponentStore implements ComponentStore {
    private static final String STORAGE_DIR = System.getProperty("user.home") + "/webtool/components/";
    private ObjectMapper mapper = new ObjectMapper();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private RemoteResourceHelper<String> resourceHelper;
    private MessageDigest digest;

    @Inject
    public FileSystemComponentStore(Client client, RemoteResourceHelperFactory resourceHelperFactory) throws Exception {
        digest = MessageDigest.getInstance("md5");
        resourceHelper = resourceHelperFactory.createResourceHelper(client, String.class);
        File dir = new File(STORAGE_DIR);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                logger.warn("Unable to create component store storage directory: {}", STORAGE_DIR);
            }
        }
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
        mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
    }

    public Component getComponent(String name) throws Exception {
        digest.reset();
        logger.debug("Reading component: {}", name);
        digest.update(name.getBytes());
        BigInteger hash = new BigInteger(1, digest.digest());
        String fileName = hash.toString(16);
        File componentFile = new File(STORAGE_DIR + fileName);
        logger.debug("Reading file: {}", componentFile.toURI().toASCIIString());
        if (componentFile.exists()) {
            FileReader reader = new FileReader(componentFile);
            Component component = mapper.readValue(reader, Component.class);
            Content content = component.content;
            if (content.uri != null) {
                Versioned<String> versioned = resourceHelper.getComponent(component.content.uri);
                component.content.content = versioned.resource;
                component.content.version = versioned.version.getValue();
            }
            return component;
        }
        return null;
    }

    public void storeComponent(String name, Component component) throws Exception {
        digest.reset();
        logger.debug("Writing component: {}", name);
        component.version = UUID.randomUUID().toString();
        digest.update(name.getBytes());
        BigInteger hash = new BigInteger(1, digest.digest());
        String fileName = hash.toString(16);
        File componentFile = new File(STORAGE_DIR + fileName);
        logger.debug("Writing file: {}", componentFile.toURI().toASCIIString());
        FileWriter writer = new FileWriter(componentFile);
        mapper.writeValue(writer, component);
        writer.close();
    }
}
