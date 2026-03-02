package com.dateclick.api.infrastructure.monitoring

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class SlackNotificationService(
    restTemplateBuilder: RestTemplateBuilder,
    @Value("\${monitoring.slack.webhook-url:}") private val webhookUrl: String,
) {
    private val logger = LoggerFactory.getLogger(SlackNotificationService::class.java)
    private val restTemplate: RestTemplate = restTemplateBuilder.build()
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
            logger.error("Slack 알림 전송 실패: {}", e.message)
        }
    }

    private fun buildPayload(ex: Exception): Map<String, Any> {
        val timestamp = LocalDateTime.now().format(timeFormatter)
        val text = buildString {
            append(":rotating_light: *서버 에러 발생*\n")
            append(">*유형*: `${ex.javaClass.simpleName}`\n")
            append(">*메시지*: ${ex.message?.take(300) ?: "알 수 없는 오류"}\n")
            append(">*시간*: $timestamp")
        }
        return mapOf("text" to text)
    }
}
