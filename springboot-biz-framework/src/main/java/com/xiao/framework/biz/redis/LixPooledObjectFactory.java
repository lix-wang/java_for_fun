package com.xiao.framework.biz.redis;

import com.xiao.framework.biz.exception.LixRuntimeException;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.net.URI;

/**
 * Pooled object factory.
 *
 * @author lix wang
 */
public class LixPooledObjectFactory {
    private static final Logger logger = LogManager.getLogger(LixPooledObjectFactory.class);

    public static PooledObjectFactory<Jedis> getPooledObjectFactory(final URI uri, final int connectionTimeout,
            final int socketTimeout) {
        PooledObjectFactory factory;
        try {
            Class clazz = Class.forName("redis.clients.jedis.JedisFactory");
            Class[] parameters = new Class[]{URI.class, Integer.class, Integer.class, String.class};
            factory = (PooledObjectFactory) clazz.getDeclaredConstructor(parameters).newInstance(uri,
                    connectionTimeout, socketTimeout, null);
        } catch (Exception ex) {
            logger.error("Get pooled object factory failed.", ex);
            throw LixRuntimeException.builder()
                    .message("Failed to create jedis pooled object factory").build();
        }
        return factory;
    }
}
