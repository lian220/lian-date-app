package com.dateclick.api.infrastructure.config

import com.dateclick.api.infrastructure.ratelimit.RateLimitInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Rate Limit 설정
 */
@Configuration
class RateLimitConfig(
    private val rateLimitInterceptor: RateLimitInterceptor,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(rateLimitInterceptor)
            .addPathPatterns("/v1/**")
            .excludePathPatterns(
                "/v1/health",
                "/v1/regions",
                "/v1/regions/**",
            )
    }
}
