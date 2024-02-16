package com.redislabs.university.RU102J.dao;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Random;

public class RateLimiterSlidingDaoRedisImpl implements RateLimiter {

    public static final int PAST_YEAR = 2023;
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

            Pipeline pipeline = jedis.pipelined();
            //Transaction transaction = jedis.multi();
            long currentTimeMillis = System.currentTimeMillis();
            Random random = new Random();
            pipeline.zadd(key, currentTimeMillis, currentTimeMillis + "-" + random.nextDouble());
            pipeline.zremrangeByScore(key, LocalDate.of(PAST_YEAR, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli() , currentTimeMillis - this.windowSizeMS);

            // Execute the transaction and get the actual string values
            pipeline.sync();

            if (jedis.zcard(key) > maxHits) {
                throw new RateLimitExceededException();
            }

        }
    }

    private String getKey(String name) {
        return RedisSchema.getRateSlidingLimiterKey(name, windowSizeMS, maxHits);
    }
    

        // END CHALLENGE #7
}
