package mediaManager.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisService(private val redisTemplate: RedisTemplate<String, String>) {
    private val redisPrefix = "dates:media_manager"

    fun set(
        context: RedisContext,
        key: String,
        value: String,
        expirationTime: Long = 86400, // 1 Day
    ) {
        redisTemplate.opsForValue().set(this.getContextKey(context, key), value, expirationTime, TimeUnit.SECONDS)
    }

    fun get(
        context: RedisContext,
        key: String,
    ): String? {
        return redisTemplate.opsForValue().get(getContextKey(context, key))
    }

    fun delete(
        context: RedisContext,
        key: String,
    ) {
        redisTemplate.delete(getContextKey(context, key))
    }

    private fun getContextKey(
        context: RedisContext,
        key: String,
    ) = "$redisPrefix:${context.name}_$key"
}
