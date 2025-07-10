package com.example.learning.RateLimiter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/*
* This class service implements sliding window rate limiter with help of Redis Data Store.
* 1) Use sorted set in Redis, and with help of zremrangeByScore remove past members having score
* less than past 60s(Actual time is stored in millis).
* 2) Once, the past expired items are removed use ZCOUNT and check for validity
* 3) Also, set the EXPIRE timer for key to window size. In Redis setting expire only will reset the TTL, so when the key
* is not inserted again i.e no request is made, we can remove it and save storage.
* 4) One drawback with this is that it is memory intensive, that is we have to store for multiple timestamps.
* */
@Service("slidingWindowLog")
public class SlidingWindowLogRateLimiter implements RateLimiter{

    private final JedisPool jedisPool;
    private final int maxRequests;
    private final int windowSize;

    public SlidingWindowLogRateLimiter(
            JedisPool jedisPool,
            @Value("${redis.max-requests}") int maxRequests,
            @Value("${redis.window-size}") int windowSize
    ) {
        this.jedisPool = jedisPool;
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
    }

    public boolean isAllowed(String userKey) {
        long now = System.currentTimeMillis();
        long windowStart = now - windowSize * 1000L;

        try (Jedis jedis = jedisPool.getResource()) {
            String redisKey = "rate_limit:" + userKey;

            // Remove old timestamps
            jedis.zremrangeByScore(redisKey, 0, windowStart);

            // Check request count
            long count = jedis.zcard(redisKey);
            if (count >= maxRequests) {
                return false;
            }

            jedis.zadd(redisKey, now, String.valueOf(now));

            // Set key expiration
            jedis.expire(redisKey, windowSize);

            return true;
        }
    }
}
