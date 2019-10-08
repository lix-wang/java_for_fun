package com.xiao.demo.server.demo.biz;

import com.xiao.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

/**
 * Demo cache.
 * SpEL expression.
 *
 * @author lix wang
 */
public class TestCache {
    @Cacheable(cacheNames = "str_value", key = "#name")
    public String getStr(String name, boolean isReturn) {
        return name;
    }

    @Cacheable(cacheNames = "cache_id", key = "#user.id", condition = "#user.name != 'manager'")
    public long getId(User user) {
        return user.getId();
    }

    @Caching(evict = {@CacheEvict("primary"), @CacheEvict(cacheNames = "name", key = "#p0")})
    public void evictCache() {
    }
}
