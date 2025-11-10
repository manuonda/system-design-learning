package com.manuonda.urlshortener.config;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

   @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
       //Configuration serialization
       ObjectMapper  objectMapper = new ObjectMapper();
       objectMapper.activateDefaultTyping(
               objectMapper.getPolymorphicTypeValidator(),
               ObjectMapper.DefaultTyping.NON_FINAL,
               JsonTypeInfo.As.PROPERTY);

       GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer
                = new GenericJackson2JsonRedisSerializer(objectMapper);

       //Configuration of cache
       RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
       RedisCacheConfiguration redisCacheConfiguration = config
               .entryTtl(Duration.ofMinutes(10)) // Time to live for cache entries
               .serializeKeysWith(
                       RedisSerializationContext.SerializationPair
                               .fromSerializer(new StringRedisSerializer()))
               .serializeValuesWith(RedisSerializationContext.SerializationPair
                       .fromSerializer(jackson2JsonRedisSerializer))
               .disableCachingNullValues();

       return RedisCacheManager.builder(factory)
               .cacheDefaults(redisCacheConfiguration)
               .build();
   }

   @Bean
   public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
      RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
      redisTemplate.setConnectionFactory(factory);

      //Serialization keys(String)
      redisTemplate.setKeySerializer(new StringRedisSerializer());
      redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

      // Serialización de values (JSON)
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.activateDefaultTyping(
              objectMapper.getPolymorphicTypeValidator(),
              ObjectMapper.DefaultTyping.NON_FINAL,
              JsonTypeInfo.As.PROPERTY
      );
      GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer =
              new GenericJackson2JsonRedisSerializer(objectMapper);

      redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
      redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
      redisTemplate.afterPropertiesSet();

      return redisTemplate;

   }

}
