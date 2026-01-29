package com.dateclick.api.infrastructure.ratelimit

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

@Service
class RateLimitService {
    private val logger = LoggerFactory.getLogger(javaClass)

    private data class RequestCounter(
        @Volatile var windowStart: Long,
        val count: AtomicInteger = AtomicInteger(0),
    ) {
        fun increment() = count.incrementAndGet()

        fun reset(newWindowStart: Long) {
            windowStart = newWindowStart
            count.set(0)
        }
    }

    private data class RateLimitRule(
        val limit: Int,
        val windowSeconds: Int,
    )

    private val rateLimitCache: Cache<String, RequestCounter> =
        Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(2))
            .maximumSize(10000)
            .build()

    private val rateLimitRules =
        mapOf(
            "POST:/v1/courses" to RateLimitRule(limit = 10, windowSeconds = 60),
            "POST:/v1/courses/*/regenerate" to RateLimitRule(limit = 5, windowSeconds = 60),
            "DEFAULT" to RateLimitRule(limit = 100, windowSeconds = 60),
        )

    fun checkRateLimit(
        sessionId: String,
        method: String,
        endpoint: String,
    ) {
        val normalizedEndpoint = normalizeEndpoint(endpoint)
        val key = "$sessionId:$method:$normalizedEndpoint"
        val rule = findMatchingRule(method, normalizedEndpoint)

        val now = System.currentTimeMillis()
        val counter = rateLimitCache.get(key) { RequestCounter(now) }

        synchronized(counter) {
            if (now - counter.windowStart >= rule.windowSeconds * 1000) {
                counter.reset(now)
            }

            if (counter.count.get() >= rule.limit) {
                val retryAfterSeconds = (rule.windowSeconds - ((now - counter.windowStart) / 1000)).coerceAtLeast(1)
                logger.warn(
                    "Rate limit exceeded: sessionId={}, endpoint={}, limit={}/{} seconds",
                    sessionId,
                    "$method:$normalizedEndpoint",
                    rule.limit,
                    rule.windowSeconds,
                )
                throw RateLimitException(
                    endpoint = "$method:$normalizedEndpoint",
                    limit = rule.limit,
                    windowSeconds = rule.windowSeconds,
                    retryAfterSeconds = retryAfterSeconds,
                )
            }

            counter.increment()
            logger.debug(
                "Rate limit check passed: sessionId={}, endpoint={}, count={}/{}",
                sessionId,
                "$method:$normalizedEndpoint",
                counter.count.get(),
                rule.limit,
            )
        }
    }

    private fun normalizeEndpoint(endpoint: String): String {
        // 특정 패턴의 Path Variable을 * 로 치환
        // 예: /v1/courses/123/regenerate -> /v1/courses/*/regenerate
        return endpoint
            .replace(Regex("/courses/[^/]+/"), "/courses/*/") // /courses/{id}/*
            .replace(Regex("/courses/[^/]+$"), "/courses/*") // /courses/{id}
            .replace(Regex("/places/[^/]+/"), "/places/*/") // /places/{id}/*
            .replace(Regex("/places/[^/]+$"), "/places/*") // /places/{id}
    }

    private fun findMatchingRule(
        method: String,
        normalizedEndpoint: String,
    ): RateLimitRule {
        val fullPath = "$method:$normalizedEndpoint"

        rateLimitRules.entries.forEach { (pattern, rule) ->
            if (pattern == "DEFAULT") return@forEach
            if (fullPath.matches(Regex(pattern.replace("*", "[^/]+"))) || fullPath == pattern) {
                return rule
            }
        }

        return rateLimitRules["DEFAULT"]!!
    }
}
