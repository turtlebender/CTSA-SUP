package org.globus.cs.render.guice;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: trhowe
 * Date: Jun 28, 2010
 * Time: 12:58:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class CachingInterceptor implements MethodInterceptor {
    private CacheManager manager;

    public CachingInterceptor(CacheManager manager) {
        this.manager = manager;
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Cached cached = methodInvocation.getMethod().getAnnotation(Cached.class);
        Object result = null;
        if (cached != null) {
            if (!manager.cacheExists(cached.cacheName())) {
                manager.addCache(cached.cacheName());
            }
            result = manager.getCache(cached.cacheName()).get(generateKey(methodInvocation));
        }
        if (result == null) {
            result = methodInvocation.proceed();
        }
        TriggersInvalidate invalidate = methodInvocation.getMethod().getAnnotation(TriggersInvalidate.class);
        if (invalidate != null) {
            if (!manager.cacheExists(invalidate.cacheName())) {
                manager.addCache(invalidate.cacheName());
            }
            Cache cache = manager.getCache(invalidate.cacheName());
            cache.remove(generateKey(methodInvocation));
        }
        return result;
    }

    private Object generateKey(MethodInvocation methodInvocation) {
        String methodName = methodInvocation.getMethod().getName();
        Object[] args = methodInvocation.getArguments();
        int result = methodName != null ? methodName.hashCode() : 0;
        if (args != null) {
            for (Object arg : args) {
                result = 31 * result + (arg != null ? arg.hashCode() : 0);
            }
        }
        return result;
    }


}
