package com.daniel.identity_service.configuration.database;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching // Bật tính năng @Cacheable, @CacheEvict của Spring
public class RedisConfig {

    /**
     * 1. RedisTemplate<String, Object>: Dùng cho các thao tác thủ công (manual ops)
     * như blacklist token, counter, session, custom key-value...
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key dùng String serializer
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());

        // Value dùng JSON serializer chuẩn mới (thay thế GenericJackson2JsonRedisSerializer)
        template.setValueSerializer(RedisSerializer.json());
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 2. RedisCacheManager: Dùng cho Spring Cache qua Annotation (@Cacheable)
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                // TTL mặc định cho cache (ví dụ: 10 phút)
                .entryTtl(Duration.ofMinutes(10))
                // Không cache giá trị null để tránh ô nhiễm bộ nhớ
                .disableCachingNullValues()
                // Key trong Redis serialize thành String (dạng "cacheName::key")
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string())
                )
                // Value serialize thành JSON chuẩn
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json())
                );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }
}