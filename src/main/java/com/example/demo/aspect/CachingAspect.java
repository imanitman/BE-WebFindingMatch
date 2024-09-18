package com.example.demo.aspect;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class CachingAspect {
    private final CacheManager cacheManager;

    private Map<String, AtomicInteger> requestCounter = new ConcurrentHashMap<>();
    private Integer limitToCache = 5;

    public CachingAspect (CacheManager cacheManager){
        this.cacheManager = cacheManager;
    }


    @Around("execution(* com.example.demo.service.*.*(..))")
    public Object cacheMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        //đặt tên key
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String key = methodName + ":" + Arrays.toString(args);
        // đếm số lần của mỗi request gọi đến server
        AtomicInteger counter = requestCounter.computeIfAbsent(key, value -> new AtomicInteger());
        counter.incrementAndGet();
        requestCounter.put(key, counter);
        Cache cache = cacheManager.getCache("myCache");
        Cache.ValueWrapper cacheValue =cache.get(key);
        if (cacheValue != null){
            return cacheValue.get();
        }
        Object result = joinPoint.proceed();
        if (requestCounter.get(key).get() > limitToCache){
            cache.put(key, result);
        }
        return result;
    }
}