package org.globus.cs.render.impl;

import com.google.inject.Inject;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.UUID;

public class DefaultPageStore implements PageStore {
    private static final String STORAGE_DIR = System.getProperty("user.home") + "/webtool/pages/";
    ObjectMapper mapper = new ObjectMapper();
    ComponentStore componentStore;
    PreloadStore preloadStore;
    Logger logger = LoggerFactory.getLogger(getClass());


    @Inject
    public DefaultPageStore(ComponentStore componentStore, PreloadStore preloadStore) {
        this.componentStore = componentStore;
        this.preloadStore = preloadStore;
        File dir = new File(STORAGE_DIR);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                logger.warn("Unable to create page store storage directory: {}", STORAGE_DIR);
            }
        }
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
        mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
    }

    public Page getPage(String name) throws Exception {
        FileReader reader = new FileReader(new File(STORAGE_DIR + name));
        Page page = mapper.readValue(reader, Page.class);
        Map<String, Component> components = page.slotMappings;
        if (components != null) {
            for (String key : components.keySet()) {
                Component component = components.get(key);
                if (component.uri != null) {
                    components.put(key, componentStore.getComponent(component.uri));
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
        FileWriter writer = new FileWriter(new File(STORAGE_DIR + name));
        mapper.writeValue(writer, page);
        writer.close();
    }
}
