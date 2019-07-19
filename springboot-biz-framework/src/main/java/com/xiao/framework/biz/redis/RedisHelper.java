package com.xiao.framework.biz.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.validation.constraints.NotNull;

/**
 * Helper for redis.
 *
 * @author lix wang
 */
public class RedisHelper {
    public static RedisService getRedisService(@NotNull String host, int port, String password) {
        return (RedisService) new RedisTemplateBaseRedisService(
                createRedisTemplate(getRedisConnectionFactory(host, port, password)));
    }

    public static <K, V> RedisTemplate<K, V> getRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory);
    }

    private static RedisConnectionFactory getRedisConnectionFactory(@NotNull String host, int port, String password) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        if (StringUtils.isNotBlank(password)) {
            configuration.setPassword(RedisPassword.of(password));
        }
        return new JedisConnectionFactory(configuration);
    }

    private static <K, V> RedisTemplate<K, V> createRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
