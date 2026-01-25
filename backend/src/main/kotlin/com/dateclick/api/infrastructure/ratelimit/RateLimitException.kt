package com.dateclick.api.infrastructure.ratelimit

/**
 * Rate Limit 초과 시 발생하는 예외
 */
class RateLimitException(
    val endpoint: String,
    val limit: Int,
    val windowSeconds: Int,
    val retryAfterSeconds: Long
) : RuntimeException("Rate limit exceeded for $endpoint: $limit requests per ${windowSeconds}s")
