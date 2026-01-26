package com.dateclick.api.infrastructure.external.openai

import com.dateclick.api.infrastructure.config.OpenAiProperties
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

/**
 * OpenAI API 클라이언트
 * Chat Completion API를 호출하여 AI 기반 코스 추천을 수행
 */
@Component
class OpenAiClient(
    private val openAiRestClient: RestClient,
    private val openAiProperties: OpenAiProperties,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Chat Completion API 호출
     *
     * @param request ChatCompletion 요청
     * @return ChatCompletion 응답
     * @throws OpenAiException API 호출 실패 시
     */
    fun createChatCompletion(request: ChatCompletionRequest): ChatCompletionResponse {
        logger.debug("Calling OpenAI Chat Completion API with model: {}", request.model)

        return try {
            val response =
                openAiRestClient.post()
                    .uri("/v1/chat/completions")
                    .header("Authorization", "Bearer ${openAiProperties.apiKey}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError) { _, response ->
                        val errorBody = response.body?.let { String(it.readAllBytes()) }
                        logger.error("OpenAI API error: status={}, body={}", response.statusCode, errorBody)
                        throw OpenAiException("OpenAI API call failed: ${response.statusCode}", errorBody)
                    }
                    .body(ChatCompletionResponse::class.java)
                    ?: throw OpenAiException("Empty response from OpenAI API")

            logger.info(
                "OpenAI API call successful. tokens used: {} (prompt: {}, completion: {})",
                response.usage.totalTokens,
                response.usage.promptTokens,
                response.usage.completionTokens,
            )

            response
        } catch (ex: OpenAiException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Unexpected error calling OpenAI API", ex)
            throw OpenAiException("Unexpected error calling OpenAI API: ${ex.message}", null, ex)
        }
    }
}

/**
 * OpenAI API 호출 실패 예외
 */
class OpenAiException(
    message: String,
    val errorBody: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
