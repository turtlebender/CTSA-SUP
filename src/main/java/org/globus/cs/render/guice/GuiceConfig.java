package org.globus.cs.render.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.globus.cs.render.ComponentStore;
import org.globus.cs.render.PageStore;
import org.globus.cs.render.PreloadStore;
import org.globus.cs.render.RemoteResourceHelperFactory;
import org.globus.cs.render.impl.*;
import org.globus.cs.render.rest.ComponentContentProvider;
import org.globus.cs.render.rest.JSONPProvider;
import org.globus.cs.render.rest.JSONProvider;

import javax.servlet.ServletContextEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class GuiceConfig extends GuiceServletContextListener {
    private String componentPath;
    private String pagesPath;


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        componentPath = servletContextEvent.getServletContext().getRealPath("/");
        pagesPath = servletContextEvent.getServletContext().getRealPath("/");
        super.contextInitialized(servletContextEvent);
    }

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new JerseyServletModule() {


            @Override
            protected void configureServlets() {
                bind(RemoteResourceHelperFactory.class).to(HashMapRemoteResourceHelperFactory.class);
                ClientConfig config = new DefaultClientConfig();
                config.getClasses().add(JSONProvider.class);
                config.getClasses().add(JSONPProvider.class);
                config.getClasses().add(ComponentContentProvider.class);
                Client client = Client.create(config);
                bind(Client.class).toInstance(client);
                bind(File.class).annotatedWith(RealComponentPath.class).toInstance(new File(componentPath));
                bind(File.class).annotatedWith(RealPagePath.class).toInstance(new File(pagesPath));
                bind(ComponentStore.class).annotatedWith(Local.class).to(FileSystemComponentStore.class);
                bind(ComponentStore.class).annotatedWith(Remote.class).to(RemoteComponentStore.class);
                bind(ComponentStore.class).to(DefaultComponentStore.class);

                bind(PreloadStore.class).to(DefaultPreloadStore.class);

                bind(PageStore.class).to(DefaultPageStore.class);
                
                Map<String, String> params = new HashMap<String, String>();

                params.put(PackagesResourceConfig.PROPERTY_PACKAGES, "org.globus.cs.render.rest");

                serveRegex("(/.*.pagedesc)","(/.*.desc)").with(GuiceContainer.class, params);                
            }
        });
    }
}
