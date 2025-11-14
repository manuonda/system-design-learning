package com.manuonda.urlshortener.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

   // 1. Crea un ObjectMapper centralizado y configurado.
   @Bean
   public ObjectMapper objectMapper() {
      ObjectMapper objectMapper = new ObjectMapper();

      // Habilita el soporte para tipos de fecha/hora de Java 8 (Instant).
      objectMapper.registerModule(new JavaTimeModule());

      // La anotación @JsonTypeInfo en ShortUrlCacheDto se encargará de incluir el tipo
      // No necesitamos activateDefaultTyping aquí

      return objectMapper;
   }

   // 2. Crea un GenericJackson2JsonRedisSerializer usando el ObjectMapper configurado.
   @Bean
   public GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer(ObjectMapper objectMapper) {
      return new GenericJackson2JsonRedisSerializer(objectMapper);
   }

   @Bean
   public RedisCacheManager cacheManager(
           RedisConnectionFactory factory,
           GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {

      // Configuración de caché
      RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
      RedisCacheConfiguration redisCacheConfiguration = config
              .entryTtl(Duration.ofMinutes(10)) // Tiempo de vida para las entradas del caché
              .serializeKeysWith(
                      RedisSerializationContext.SerializationPair
                              .fromSerializer(new StringRedisSerializer()))
              .serializeValuesWith(RedisSerializationContext.SerializationPair
                      .fromSerializer(jackson2JsonRedisSerializer)) // Usa el serializer bean
              .disableCachingNullValues();

      return RedisCacheManager.builder(factory)
              .cacheDefaults(redisCacheConfiguration)
              .build();
   }

   @Bean
   public RedisTemplate<String, Object> redisTemplate(
           RedisConnectionFactory factory,
           ObjectMapper objectMapper) {

      RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
      redisTemplate.setConnectionFactory(factory);

      // Serialización de keys (String)
      redisTemplate.setKeySerializer(new StringRedisSerializer());

      // Serialización de values (JSON) con @class para deserialización correcta
      // Usamos el objectMapper centralizado (con JavaTimeModule y activateDefaultTyping)
      GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer =
              new GenericJackson2JsonRedisSerializer(objectMapper);

      redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
      redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
      redisTemplate.afterPropertiesSet();

      return redisTemplate;
   }
}