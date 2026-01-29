package com.dateclick.api.infrastructure.ratelimit

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

/**
 * Rate Limiting 인터셉터
 */
@Component
class RateLimitInterceptor(
    private val rateLimitService: RateLimitService,
) : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        // X-Session-Id 헤더 확인
        val sessionId = request.getHeader("X-Session-Id")
        if (sessionId.isNullOrBlank()) {
            logger.debug("X-Session-Id header missing, skipping rate limit for {}", request.requestURI)
            return true
        }

        val method = request.method
        val endpoint = request.requestURI

        rateLimitService.checkRateLimit(sessionId, method, endpoint)
        return true
    }
}
