package com.dateclick.api.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()

        // 허용할 Origin 설정
        config.allowedOrigins = listOf(
            "http://localhost:3000",
            "http://localhost:3001",
            "http://localhost:3002"
        )

        // 허용할 HTTP 메서드
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")

        // 허용할 헤더
        config.allowedHeaders = listOf("*")

        // 인증 정보 포함 허용
        config.allowCredentials = true

        // 노출할 헤더
        config.exposedHeaders = listOf("X-Session-Id")

        // Preflight 요청 캐시 시간 (초)
        config.maxAge = 3600L

        source.registerCorsConfiguration("/**", config)

        return CorsFilter(source)
    }
}
