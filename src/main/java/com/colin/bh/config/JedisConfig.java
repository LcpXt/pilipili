package com.colin.bh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 2024年03月23日16:53
 */
@Configuration
public class JedisConfig {

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(200);
        jedisPoolConfig.setMaxWaitMillis(60000);
        return new JedisPool(jedisPoolConfig, "117.78.8.44", 6379);
    }

}
