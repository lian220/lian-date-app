package com.dateclick.api.infrastructure.monitoring

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class SlackNotificationService(
    restTemplateBuilder: RestTemplateBuilder,
    @Value("\${monitoring.slack.webhook-url:}") private val webhookUrl: String,
) {
    private val logger = LoggerFactory.getLogger(SlackNotificationService::class.java)
    private val restTemplate: RestTemplate = restTemplateBuilder
        .connectTimeout(Duration.ofSeconds(3))
        .readTimeout(Duration.ofSeconds(5))
        .build()
    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun sendErrorAlert(ex: Exception) {
        if (webhookUrl.isBlank()) return
        try {
            val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
            val payload = buildPayload(ex)
            val request = HttpEntity(payload, headers)
            restTemplate.postForObject(webhookUrl, request, String::class.java)
            logger.info("Slack 에러 알림 전송 완료: {}", ex.javaClass.simpleName)
        } catch (e: Exception) {
            logger.error("Slack 알림 전송 실패", e)
        }
    }

    private fun buildPayload(ex: Exception): Map<String, Any> {
        val timestamp = LocalDateTime.now().format(timeFormatter)
        // 민감 정보 노출 방지: 에러 유형만 전송, 원문 메시지는 제외
        val safeMessage = ex.javaClass.simpleName
        val text = buildString {
            append(":rotating_light: *서버 에러 발생*\n")
            append(">*유형*: `$safeMessage`\n")
            append(">*시간*: $timestamp")
        }
        return mapOf("text" to text)
    }
}
