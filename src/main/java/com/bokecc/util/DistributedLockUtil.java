package com.bokecc.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;

/**
 * redis分布式锁
 **/

@Slf4j
public class DistributedLockUtil {

    private static final String LUA =
            "local n = redis.call('setnx',KEYS[1],ARGV[1])\n" +
            "if n == 1 " +
               "then redis.call('expire',KEYS[1],ARGV[2]) " +
             "end\n" +
             "return n";

    /**
     * 加锁
     * @param redisTemplate redis客户端
     * @param key key
     * @param value value
     * @param expireTime 过期时间
     * @return  true：加锁成功 or false
     */
    public static boolean lock(RedisTemplate<String, Object> redisTemplate, String key, String value, int expireTime){

        DefaultRedisScript<Long> script = new DefaultRedisScript<Long>(LUA, Long.class);

        Long re = redisTemplate.execute(script, Collections.singletonList(key), value, expireTime);

        return re == 1;
    }

    /**
     * 解锁
     * @param redisTemplate 客户端
     * @param key key
     * @return true :成功 false：失败
     */
    public static boolean unLock(RedisTemplate<String, Object> redisTemplate, String key){

        try {

            redisTemplate.delete(key);

            return true;
        }catch (Exception e){

            log.error("unlock exception", e);
        }

        return false;
    }
}
