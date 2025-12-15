package com.tarasov.market.configuration;

import com.tarasov.market.model.cache.OfferingCache;
import com.tarasov.market.model.cache.OfferingPageCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, OfferingCache> offeringCacheRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, OfferingCache.class);
    }

    @Bean
    public ReactiveRedisTemplate<String, OfferingPageCache> offeringPageRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, OfferingPageCache.class);
    }

    private <T> ReactiveRedisTemplate<String, T> createRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory, Class<T> clazz) {

        RedisSerializationContext<String, T> context =
                RedisSerializationContext
                        .<String, T>newSerializationContext(new StringRedisSerializer())
                        .value(new JacksonJsonRedisSerializer<>(clazz))
                        .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}
