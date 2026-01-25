package com.dateclick.api.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Chroma DB 설정 Properties
 */
@Configuration
@ConfigurationProperties(prefix = "chroma")
data class ChromaDbProperties(
    /**
     * Chroma DB 서버 URL
     * 예: http://localhost:8000
     */
    var url: String = "http://localhost:8000",

    /**
     * 장소 메모리용 컬렉션 이름
     */
    var placeCollectionName: String = "date_places",

    /**
     * 연결 타임아웃 (밀리초)
     */
    var connectionTimeout: Long = 5000,

    /**
     * 읽기 타임아웃 (밀리초)
     */
    var readTimeout: Long = 30000,

    /**
     * 배치 처리 크기
     */
    var batchSize: Int = 10
)
