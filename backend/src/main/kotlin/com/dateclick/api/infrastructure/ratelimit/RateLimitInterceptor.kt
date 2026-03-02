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
        // X-Session-Id 헤더 확인, 없으면 IP 주소로 fallback
        val sessionId = request.getHeader("X-Session-Id")
        val identifier =
            if (!sessionId.isNullOrBlank()) {
                sessionId
            } else {
                val ip = request.getHeader("X-Forwarded-For")?.split(",")?.firstOrNull()?.trim()
                    ?: request.remoteAddr
                "ip:$ip"
            }

        val method = request.method
        val endpoint = request.requestURI

        rateLimitService.checkRateLimit(identifier, method, endpoint)
        return true
    }
}
