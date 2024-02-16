package com.redislabs.university.RU102J.dao;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import java.util.Random;

public class RateLimiterSlidingDaoRedisImpl implements RateLimiter {

    private final JedisPool jedisPool;
    private final long windowSizeMS;
    private final long maxHits;

    public RateLimiterSlidingDaoRedisImpl(JedisPool pool, long windowSizeMS,
                                          long maxHits) {
        this.jedisPool = pool;
        this.windowSizeMS = windowSizeMS;
        this.maxHits = maxHits;
    }

    // Challenge #7
    @Override
    public void hit(String name) throws RateLimitExceededException {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = getKey(name);
            //Pipeline pipeline = jedis.pipelined();
            Transaction transaction = jedis.multi();

            String score = String.format("%d", random.nextInt(1_000_000));
            transaction.zadd(key, score);

            transaction.zremrangeByScore(key, LocalDate.of(2023, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli() , System.currentTimeMillis() - this.windowSizeMS);
           

            //pipeline.sync();
            if (transaction.zcard(key) > maxHits) {
                throw new RateLimitExceededException();
            }

            // Execute the transaction and get the actual string values
            transaction.exec();
        }
    }

    private String getKey(String name) {
        return RedisSchema.getRateSlidingLimiterKey(name, windowSizeMS, maxHits);
    }
    

        // END CHALLENGE #7
}
