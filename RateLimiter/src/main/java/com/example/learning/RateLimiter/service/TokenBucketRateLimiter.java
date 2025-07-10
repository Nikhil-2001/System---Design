package com.example.learning.RateLimiter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;


/*
 * This class service implements token bucket rate limiter with help of Redis Data Store.
 * 1) Uses hash in Redis, and with help of lua script we can avoid race conditions, by executing all commands in a single
 * request.
 * 2) The algorithm refils the bucket for every 60 seconds with specified number of tokens or based on refill rate.
 * 3) When the user is requesting for first time, it inserts with max token value.
 * 4) Also, set the EXPIRE timer for key to window size. In Redis setting expire only will reset the TTL, so when the key
 * is not inserted again i.e no request is made, we can remove it and save storage.
 * 5) Token bucket is well suited for rate limiter, it has excellent burst handling capacity(Sudden high spike of requests)
 *  easy to design and most suited for the majority of API's.
 * */
@Service("tokenBucket")
public class TokenBucketRateLimiter implements RateLimiter{
    private final JedisPool jedisPool;
    private final int maxTokens;
    private final double refillRate;

    private static final String LUA_SCRIPT =
            "local bucket = redis.call('HMGET', KEYS[1], 'tokens', 'last_refill')\n" +
                    "local tokens = tonumber(bucket[1]) or tonumber(ARGV[1])\n" +
                    "local last_refill = tonumber(bucket[2]) or tonumber(ARGV[3])\n" +
                    "local current_time = tonumber(ARGV[3])\n" +
                    "local refill_rate = tonumber(ARGV[2])\n" +
                    "local max_tokens = tonumber(ARGV[1])\n" +
                    "local elapsed = current_time - last_refill\n" +
                    "local new_tokens = math.floor(elapsed / 1000 * refill_rate)\n" +
                    "tokens = math.min(tokens + new_tokens, max_tokens)\n" +
                    "if tokens > 0 then\n" +
                    "    tokens = tokens - 1\n" +
                    "    redis.call('HMSET', KEYS[1], 'tokens', tokens, 'last_refill', current_time)\n" +
                    "    redis.call('EXPIRE', KEYS[1], 60)\n" +
                    "    return 1\n" +
                    "else\n" +
                    "    redis.call('HMSET', KEYS[1], 'tokens', tokens, 'last_refill', current_time)\n" +
                    "    redis.call('EXPIRE', KEYS[1], 60)\n" +
                    "    return 0\n" +
                    "end";

    public TokenBucketRateLimiter(
            JedisPool jedisPool
    ) {
        this.jedisPool = jedisPool;
        this.maxTokens = 5;
        this.refillRate = 5.0 /60;
    }

    public boolean isAllowed(String userKey) {
        try (Jedis jedis = jedisPool.getResource()) {
            long now = System.currentTimeMillis();
            Object result = jedis.eval(
                    LUA_SCRIPT,
                    Collections.singletonList(userKey),
                    java.util.Arrays.asList(
                            String.valueOf(maxTokens),
                            String.valueOf(refillRate),
                            String.valueOf(now)
                    )
            );
            return Long.valueOf(1).equals(result);
        }
    }
}