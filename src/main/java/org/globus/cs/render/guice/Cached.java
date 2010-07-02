package org.globus.cs.render.guice;

import net.sf.ehcache.CacheManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cached {
    String cacheName() default CacheManager.DEFAULT_NAME;
}
