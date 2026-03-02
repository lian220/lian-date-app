package com.dateclick.api.infrastructure.external.langfuse

import com.dateclick.api.infrastructure.config.LangfuseProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.util.Base64

/**
 * Langfuse Score API 클라이언트
 * 사용자 평가를 Langfuse Score로 전송하여 user_satisfaction KPI 추적
 */
@Component
class LangfuseScoreClient(
    private val langfuseRestClient: RestClient,
    private val langfuseProperties: LangfuseProperties,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val authHeader: String by lazy {
        val credentials = "${langfuseProperties.publicKey}:${langfuseProperties.secretKey}"
        "Basic ${Base64.getEncoder().encodeToString(credentials.toByteArray())}"
    }

    /**
     * 사용자 만족도 점수를 Langfuse에 비동기 전송
     * @param score 1-5점 평가 점수
     * @param sessionId 연결할 사용자 세션 ID (Langfuse Score 연결 필수)
     * @param comment 평가 코멘트 (선택)
     */
    fun sendUserSatisfactionScore(
        score: Int,
        sessionId: String,
        comment: String? = null,
    ) {
        if (!langfuseProperties.enabled) return
        if (langfuseProperties.publicKey.isBlank()) return

        scope.launch {
            try {
                // 1-5점을 0.0-1.0으로 정규화 (user_satisfaction KPI)
                val normalizedValue = (score - 1) / 4.0

                val request =
                    LangfuseScoreRequest(
                        name = "user_satisfaction",
                        value = normalizedValue,
                        dataType = "NUMERIC",
                        sessionId = sessionId,
                        comment = comment,
                    )

                langfuseRestClient
                    .post()
                    .uri("/api/public/scores")
                    .header("Authorization", authHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity()

                logger.debug(
                    "Sent user_satisfaction score to Langfuse: score={}, normalized={}",
                    score,
                    normalizedValue,
                )
            } catch (ex: Exception) {
                logger.warn("Failed to send score to Langfuse: {}", ex.message)
            }
        }
    }
}

data class LangfuseScoreRequest(
    val name: String,
    val value: Double,
    @JsonProperty("dataType")
    val dataType: String,
    @JsonProperty("sessionId")
    val sessionId: String,
    val comment: String? = null,
)
