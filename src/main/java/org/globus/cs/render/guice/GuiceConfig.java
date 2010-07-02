package org.globus.cs.render.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.globus.cs.render.impl.*;

import java.util.HashMap;
import java.util.Map;


public class GuiceConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new JerseyServletModule() {

            @Override
            protected void configureServlets() {
                bind(RemoteResourceHelperFactory.class).to(HashMapRemoteResourceHelperFactory.class);

                Client client = new Client();
                bind(Client.class).toInstance(client);

//                CacheManager manager = CacheManager.getInstance();
//                bindInterceptor(Matchers.any(),
//                        Matchers.annotatedWith(Cached.class).or(Matchers.annotatedWith(TriggersInvalidate.class)),
//                        new CachingInterceptor(manager));

                bind(ComponentStore.class).annotatedWith(Local.class).to(FileSystemComponentStore.class);
                bind(ComponentStore.class).annotatedWith(Remote.class).to(RemoteComponentStore.class);
                bind(ComponentStore.class).to(DefaultComponentStore.class);

                bind(PreloadStore.class).to(DefaultPreloadStore.class);

                bind(PageStore.class).to(DefaultPageStore.class);
                
                Map<String, String> params = new HashMap<String, String>();

                params.put(PackagesResourceConfig.PROPERTY_PACKAGES, "org.globus.cs.render");

                serve("/*").with(GuiceContainer.class, params);
            }
        });
    }
}
