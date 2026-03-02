//package com.grocery.backend.config;
//
//import org.springframework.beans.factory.annotation.Configurable;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//@Configuration
//@EnableCaching
//public class RedisConfig {
//
////    @Bean
////    public RedisTemplate<String, String> stringRedisTemplate(
////            RedisConnectionFactory connectionFactory) {
////
////        RedisTemplate<String, String> template = new RedisTemplate<>();
////        template.setConnectionFactory(connectionFactory);
////
////        template.setKeySerializer(new StringRedisSerializer());
////        template.setValueSerializer(new StringRedisSerializer());
////
////        template.setHashKeySerializer(new StringRedisSerializer());
////        template.setHashValueSerializer(new StringRedisSerializer());
////
////        template.afterPropertiesSet();
////        return template;
////    }
//}