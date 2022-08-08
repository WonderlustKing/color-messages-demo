package com.chrisb.colors.prj.demo.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public final class RedisUtility {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisUtility(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<String> getAllValues() {
        List<String> allValues = new ArrayList<>();
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null) {
            keys.forEach(key -> allValues.addAll(getValues(key)));
        }
        return allValues;
    }

    public void setValue(final String key, final String value) {
        redisTemplate.opsForList().rightPush(key, value);
        redisTemplate.expire(key, 60, TimeUnit.MINUTES);
    }

    public List<String> getValues(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public void deleteAll() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null) {
            keys.forEach(this::deleteKey);
        }
    }
    public void deleteKey(final String key) {
        redisTemplate.delete(key);
    }
}
