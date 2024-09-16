// package com.example.demo.aspect;

// import java.util.Arrays;
// import java.util.Map;
// import java.util.HashMap;
// import java.time.Duration;
// import java.util.concurrent.atomic.AtomicInteger;
// import org.aspectj.lang.ProceedingJoinPoint;
// import org.aspectj.lang.annotation.Around;
// import org.aspectj.lang.annotation.Aspect;
// import org.springframework.cache.Cache;
// import org.springframework.cache.CacheManager;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.stereotype.Component;

// @Aspect
// @Component
// public class CachingAspect {

//     private final RedisTemplate<String, Object> redisTemplate;
//     private int limitToCache = 10;
//     private Duration duration = Duration.ofMinutes(1000);
//     private Map<String, AtomicInteger> requestCounter = new HashMap<>();
//     public CachingAspect(RedisTemplate<String, Object> redisTemplate){
//         this.redisTemplate = redisTemplate;
//     }

//     @Around("execution(* com.example.demo.service.*.*(..))")
//     public Object cacheMethod(ProceedingJoinPoint joinPoint) throws Throwable {
//         //đặt tên key
//         String methodName = joinPoint.getSignature().getName();
//         Object[] args = joinPoint.getArgs();
//         String key = methodName + ":" + Arrays.toString(args);
//         // đếm số lần của mỗi request gọi đến server
//         AtomicInteger counter = requestCounter.computeIfAbsent(key, value -> new AtomicInteger());
//         counter.incrementAndGet();
//         requestCounter.put(key, counter);
//         if (this.redisTemplate.opsForValue().get(key) != null){
//             return this.redisTemplate.opsForValue().get(key);
//         }
//         Object result = joinPoint.proceed();

//         // local date thành String
        
//         // tối ưu caching
//         if ((requestCounter.get(key)).get() > limitToCache){

//             this.redisTemplate.opsForValue().set(key,result,duration);
//             limitToCache += 3;
//             duration = duration.minus(Duration.ofMinutes(5));
//         }
//         return result;
//     }
// }