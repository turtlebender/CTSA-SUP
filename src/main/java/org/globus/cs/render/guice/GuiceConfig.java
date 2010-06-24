package org.globus.cs.render.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.globus.cs.render.PreRenderingService;
import org.globus.cs.render.impl.RhinoRenderingService;
import org.globus.cs.render.rest.PageResource;

import java.util.HashMap;
import java.util.Map;


public class GuiceConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new JerseyServletModule() {

            @Override
            protected void configureServlets() {
                bind(PreRenderingService.class).to(RhinoRenderingService.class);
                bind(PageResource.class);
                Map<String, String> params = new HashMap<String, String>();
                params.put(PackagesResourceConfig.PROPERTY_PACKAGES, "unbound");
                serve("/pages/*").with(GuiceContainer.class, params);
            }
        });
    }
}
