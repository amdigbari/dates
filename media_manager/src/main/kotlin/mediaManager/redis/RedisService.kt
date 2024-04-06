package mediaManager.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisService(private val redisTemplate: RedisTemplate<String, String>) {
    private val redisPrefix = "dates:media_manager"

    /**
     * Sets a value to redis.
     *
     * @param context RedisContext. The main context of the redis use as prefix for the key.
     * @param key String. The rest of the key. The final key will be $redisPrefix:$context_$key
     * @param value String
     * @param expirationTime Long. The value should be in seconds.
     */
    fun set(
        context: RedisContext,
        key: String,
        value: String,
        expirationTime: Long = 86400, // 1 Day
    ) {
        redisTemplate.opsForValue().set(this.getContextKey(context, key), value, expirationTime, TimeUnit.SECONDS)
    }

    /**
     * Gets a value from redis.
     *
     * @param context RedisContext. The main context of the redis use as prefix for the key.
     * @param key String. The rest of the key. The final key will be $redisPrefix:$context_$key
     *
     * @return String. The value in case key exists.
     */
    fun get(
        context: RedisContext,
        key: String,
    ): String? {
        return redisTemplate.opsForValue().get(getContextKey(context, key))
    }

    /**
     * Deletes a value from redis in case key exists
     *
     * @param context RedisContext. The main context of the redis use as prefix for the key.
     * @param key String. The rest of the key. The final key will be $redisPrefix:$context_$key
     *
     */
    fun delete(
        context: RedisContext,
        key: String,
    ) {
        redisTemplate.delete(getContextKey(context, key))
    }

    /**
     * Gets the redis saved key from context and provided key.
     *
     * @param context RedisContext. The main context of the redis use as prefix for the key.
     * @param key String. The rest of the key. The final key will be $redisPrefix:$context_$key
     *
     * @return String. $redisPrefix:$context_$key
     */
    private fun getContextKey(
        context: RedisContext,
        key: String,
    ) = "$redisPrefix:${context.name}_$key"
}
