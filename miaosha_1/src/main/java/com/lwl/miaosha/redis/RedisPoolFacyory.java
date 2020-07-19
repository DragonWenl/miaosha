package com.lwl.miaosha.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @description:
 * @author: liwenlong
 * @create: 2020-07-16
 */
@Service
public class RedisPoolFacyory {

    @Autowired
    RedisConfig redisConfig;

    @Bean
    public JedisPool JedisFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisConfig.getPoolMaxldle());
        jedisPoolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisConfig.getHost(), redisConfig.getPort(), redisConfig.getTimeout() * 1000);
        return jedisPool;
    }
}
