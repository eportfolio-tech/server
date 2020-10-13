package tech.eportfolio.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.redis.host}")
    private String redisHostName;

    @Value("${spring.redis.port}")
    private int redisPort;


    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHostName, redisPort);
        logger.info("redis host: " + redisStandaloneConfiguration.getHostName());
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> {
            Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
            configurationMap.put("users", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(2)));
            configurationMap.put("portfolios", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)));
            builder.withInitialCacheConfigurations(configurationMap);
        };
    }


}
