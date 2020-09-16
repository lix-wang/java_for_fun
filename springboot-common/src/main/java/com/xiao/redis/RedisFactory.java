package com.xiao.redis;

import com.xiao.config.CommonConfig;
import com.xiao.framework.biz.environment.CmdLineConfig;
import com.xiao.framework.biz.environment.ProfileType;
import com.xiao.framework.redis.jedis.RedisHelper;
import com.xiao.framework.redis.jedis.RedisService;
import com.xiao.framework.redis.jedis.RedisWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

/**
 * Configuration for redis.
 * <p>
 * Here, I config a wrong master redis and a good alternative redis.
 *
 * @author lix wang
 */
@Component
public class RedisFactory {
    private final CmdLineConfig cmdLineConfig;

    @Autowired
    public RedisFactory(CmdLineConfig cmdLineConfig) {
        this.cmdLineConfig = cmdLineConfig;
    }

    @Bean("defaultRedis")
    public RedisService createDefaultRedis(CommonConfig commonConfig) {
        // if dev environment return fakeRedisService
        if (ProfileType.DEV == cmdLineConfig.getProfile()) {
            return new FakeRedisService();
        }

        RedisWrapper master = RedisWrapper.builder()
                .host("127.0.0.1")
                .port(6789).build();

        LinkedList<RedisWrapper> alternatives = new LinkedList<RedisWrapper>() {
            {
                add(RedisWrapper.builder().host(commonConfig.getRedisHost()).port(commonConfig.getRedisPort()).build());
            }
        };
        return RedisHelper.getRedisService(master, alternatives);
    }
}
