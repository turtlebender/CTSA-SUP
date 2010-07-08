package org.globus.cs.render.impl;

import com.google.inject.Inject;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.globus.cs.render.ComponentStore;
import org.globus.cs.render.PageStore;
import org.globus.cs.render.PreloadStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

public class DefaultPageStore implements PageStore {
    private final File STORAGE_DIR;
    ObjectMapper mapper = new ObjectMapper();
    ComponentStore componentStore;
    PreloadStore preloadStore;
    Logger logger = LoggerFactory.getLogger(getClass());


    @Inject
    public DefaultPageStore(@RealPagePath File pagesPath, ComponentStore componentStore, PreloadStore preloadStore) {
        STORAGE_DIR = pagesPath;
        this.componentStore = componentStore;
        this.preloadStore = preloadStore;
        if (!STORAGE_DIR.exists()) {
            if (!STORAGE_DIR.mkdirs()) {
                logger.warn("Unable to create page store storage directory: {}", STORAGE_DIR);
            }
        }
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
        mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
    }

    public Page getPage(UriBuilder baseBuilder, UriBuilder builder, String name) throws Exception {
        FileReader reader = new FileReader(new File(STORAGE_DIR, name));
        Page page = mapper.readValue(reader, Page.class);
        Map<String, Component> components = page.slotMappings;
        if (components != null) {
            for (String key : components.keySet()) {
                Component component = components.get(key);
                if (component.uri != null) {
                    if (component.uri.startsWith("/")) {
                        components.put(key, componentStore.getComponent(baseBuilder.clone(), builder.clone(), component.uri));
                    } else {
                        components.put(key, componentStore.getComponent(baseBuilder.clone(), builder.clone(), component.uri));
                    }
                }
            }
        }
        Map<String, Preload> preloads = page.preload;
        if (preloads != null) {
            for (String key : preloads.keySet()) {
                Preload preload = preloads.get(key);
                if (preload.uri != null) {
                    preloads.put(key, preloadStore.getPreload(preload.uri));
                }
            }
        }
        return page;
    }

    public void storePage(String name, Page page) throws Exception {
        page.version = UUID.randomUUID().toString();
        File pageFile = new File(STORAGE_DIR, name);
        File parent = pageFile.getParentFile();
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                logger.info("Unable to create path: " + name);
                throw new IllegalArgumentException("Unable to create path: " + name);
            }
        }
        FileWriter writer = new FileWriter(pageFile);
        mapper.writeValue(writer, page);
        writer.close();
    }
}
