package com.example.learning.RateLimiter.service;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


/*
* Leaky bucket has two interpreations this is one is simpler token like.
* The main one is queue based. In this approach we fill the queue one by one, if the queue is full we drop it. At the same
* time there has to be a parallel worker thread running to deqeue from the list at constant leaky rate.
*
* In the queue based approach which will be helpful in the ticketing services, e-commerce etc...
* Add all the requests to queue i.e List in Redis from RPUSH and if size greater drop.
* With the help of scheduler run for elaky rate and execute it.
* */
@Service("leakyBucket")
public class LeakyBucketRateLimiter implements RateLimiter {

    private final JedisPool jedisPool;

    private final int capacity = 10;           // max bucket size
    private final int leakRatePerSecond = 1;   // leak rate (1 per second)

    public LeakyBucketRateLimiter(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public boolean isAllowed(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            long currentTimeMillis = System.currentTimeMillis();
            String bucketKey = "leaky:" + key;

            // Leak old requests
            long leakWindowMillis = capacity * 1000L / leakRatePerSecond;
            long threshold = currentTimeMillis - leakWindowMillis;
            jedis.zremrangeByScore(bucketKey, 0, threshold);

            long size = jedis.zcard(bucketKey);
            if (size >= capacity) {
                return false; // Bucket full
            }

            // Add new request timestamp
            jedis.zadd(bucketKey, currentTimeMillis, String.valueOf(currentTimeMillis));
            jedis.expire(bucketKey, 60); // auto-expire key
            return true;
        }
    }
}

// Addin the additional part for queue based implementation.


//@Service
//public class LeakyBucketQueueService {
//
//    private final JedisPool jedisPool;
//    private static final int MAX_CAPACITY = 10;
//    private static final String PREFIX = "leaky:queue:";
//
//    @Autowired
//    public LeakyBucketQueueService(JedisPool jedisPool) {
//        this.jedisPool = jedisPool;
//    }
//
//    private static final String PREFIX = "leaky:queue:";
//
//    public boolean tryEnqueue(String clientId) {
//        try (Jedis jedis = jedisPool.getResource()) {
//            String key = PREFIX + clientId;
//
//            long queueSize = jedis.llen(key);
//            if (queueSize >= MAX_CAPACITY) {
//                return false; // Bucket full — reject request
//            }
//
//            jedis.rpush(key, String.valueOf(System.currentTimeMillis()));
//            jedis.expire(key, 60); // Optional TTL to clean up
//            return true;
//        }
//    }
//
//    public String dequeue(String clientId) {
//        try (Jedis jedis = jedisPool.getResource()) {
//            String key = PREFIX + clientId;
//            return jedis.lpop(key);
//        }
//    }
//}

//@RestController
//public class LeakyBucketController {
//
//    @Autowired
//    private LeakyBucketQueueService bucketService;
//
//    @GetMapping("/hello")
//    public ResponseEntity<String> sayHello(HttpServletRequest request) {
//        String clientIp = request.getRemoteAddr();
//        boolean accepted = bucketService.tryEnqueue(clientIp);
//        if (!accepted) {
//            return ResponseEntity.status(429)
//                .body("Too many requests - bucket full");
//        }
//
//        return ResponseEntity.accepted().body("Request accepted and queued.");
//    }
//}
//@Component
//public class LeakyBucketWorker {
//
//    @Autowired
//    private LeakyBucketQueueService bucketService;
//
//    private final String clientId = "127.0.0.1"; // replace with loop over all clients if needed
//
//    @Scheduled(fixedRate = 200) // every 200ms → 5 requests/sec
//    public void processQueue() {
//        String request = bucketService.dequeue(clientId);
//        if (request != null) {
//            System.out.println("Processed request at: " + System.currentTimeMillis());
//        }
//    }
//}