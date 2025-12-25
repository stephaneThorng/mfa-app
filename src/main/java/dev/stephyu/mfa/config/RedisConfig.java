package dev.stephyu.mfa.config;

import dev.stephyu.mfa.adapters.out.cache.redis.RedisOtpEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
@Profile({"redis"})
public class RedisConfig {

    @Bean
    public RedisTemplate<String, RedisOtpEntity> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisOtpEntity> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key serializer
        template.setKeySerializer(new StringRedisSerializer());

        // Value serializer avec ObjectMapper
        JacksonJsonRedisSerializer<RedisOtpEntity> serializer = new JacksonJsonRedisSerializer<>(RedisOtpEntity.class);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}