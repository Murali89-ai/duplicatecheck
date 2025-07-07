package com.wu.euwallet.duplicatecheck.config;

import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisConfigProperties redisConfigProperties;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisConfigProperties.getReadWriteRedisHost());
        redisConfig.setPort(redisConfigProperties.getReadWriteRedisHostPort());
        redisConfig.setDatabase(redisConfigProperties.getDatabaseIndex());

        if (redisConfigProperties.getPassword() != null && !redisConfigProperties.getPassword().isBlank()) {
            redisConfig.setPassword(RedisPassword.of(redisConfigProperties.getPassword()));
        }

        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setBlockWhenExhausted(redisConfigProperties.isBlockWhenExhausted());
        poolConfig.setMaxIdle(redisConfigProperties.getMaxIdle());
        poolConfig.setMaxTotal(redisConfigProperties.getMaxTotal());
        poolConfig.setMinIdle(redisConfigProperties.getMinIdle());
        poolConfig.setMaxWait(Duration.ofMillis(redisConfigProperties.getMaxWaitMillis()));
        poolConfig.setMinEvictableIdleTime(Duration.ofMillis(redisConfigProperties.getMinEvictableIdleTimeMillis()));
        poolConfig.setNumTestsPerEvictionRun(redisConfigProperties.getNumTestsPerEvictionRun());
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(redisConfigProperties.getTimeBetweenEvictionRunsMillis()));
        poolConfig.setTestOnBorrow(redisConfigProperties.isTestOnBorrow());
        poolConfig.setTestOnReturn(redisConfigProperties.isTestOnReturn());
        poolConfig.setTestWhileIdle(redisConfigProperties.isTestWhileIdle());

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(5))
                .shutdownTimeout(Duration.ofMillis(100))
                .build();

        return new LettuceConnectionFactory(redisConfig, clientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
