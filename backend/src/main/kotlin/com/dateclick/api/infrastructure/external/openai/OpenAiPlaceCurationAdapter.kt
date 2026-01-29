package com.dateclick.api.infrastructure.external.openai

import com.dateclick.api.domain.place.entity.Place
import com.dateclick.api.domain.place.port.PlaceCurationPort
import com.dateclick.api.domain.place.vo.PlaceCurationInfo
import com.dateclick.api.infrastructure.config.OpenAiProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * OpenAI 기반 장소 큐레이션 어댑터
 * PlaceCurationPort 인터페이스의 구현체
 */
@Component
class OpenAiPlaceCurationAdapter(
    private val openAiClient: OpenAiClient,
    private val openAiProperties: OpenAiProperties,
    private val objectMapper: ObjectMapper,
) : PlaceCurationPort {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun curatePlace(place: Place): PlaceCurationInfo {
        logger.info("Curating place: name={}, category={}", place.name, place.category)

        try {
            // 1. 프롬프트 생성
            val messages = PlaceCurationPromptBuilder.buildMessages(place)

            // 2. OpenAI API 요청 생성
            val request =
                ChatCompletionRequest(
                    model = openAiProperties.model,
                    messages = messages,
                    temperature = 0.7,
                    maxTokens = 500,
                    responseFormat = ResponseFormat(type = "json_object"),
                )

            // 3. OpenAI API 호출
            val response = openAiClient.createChatCompletion(request)

            // 4. 응답 파싱
            val curationResponse = parseResponse(response)

            // 5. Domain 객체로 변환
            val curationInfo =
                PlaceCurationInfo(
                    dateScore = curationResponse.dateScore,
                    moodTags = curationResponse.moodTags,
                    priceRange = curationResponse.priceRange,
                    bestTime = curationResponse.bestTime,
                    recommendation = curationResponse.recommendation,
                )

            logger.info(
                "Successfully curated place: name={}, dateScore={}",
                place.name,
                curationInfo.dateScore,
            )

            return curationInfo
        } catch (ex: PlaceCurationException) {
            // parseResponse()에서 발생한 PlaceCurationException은 그대로 전파
            throw ex
        } catch (ex: OpenAiException) {
            logger.error("Failed to curate place due to OpenAI API error", ex)
            throw PlaceCurationException("장소 큐레이션 중 OpenAI API 오류가 발생했습니다: ${ex.message}", ex)
        } catch (ex: Exception) {
            logger.error("Unexpected error while curating place", ex)
            throw PlaceCurationException("장소 큐레이션 중 예상치 못한 오류가 발생했습니다: ${ex.message}", ex)
        }
    }

    /**
     * OpenAI 응답을 파싱하여 PlaceCurationResponse로 변환
     */
    private fun parseResponse(response: ChatCompletionResponse): PlaceCurationResponse {
        if (response.choices.isEmpty()) {
            throw PlaceCurationException("OpenAI 응답에 선택지가 없습니다")
        }

        val messageContent = response.choices.first().message.content
        logger.debug("OpenAI curation response: {}", messageContent)

        return try {
            objectMapper.readValue<PlaceCurationResponse>(messageContent)
        } catch (ex: Exception) {
            logger.error("Failed to parse OpenAI curation response: {}", messageContent, ex)
            throw PlaceCurationException("OpenAI 응답 파싱 실패: ${ex.message}", ex)
        }
    }
}

/**
 * OpenAI 응답 DTO
 */
data class PlaceCurationResponse(
    @JsonProperty("date_score")
    val dateScore: Int,
    @JsonProperty("mood_tags")
    val moodTags: List<String>,
    @JsonProperty("price_range")
    val priceRange: Int,
    @JsonProperty("best_time")
    val bestTime: String,
    @JsonProperty("recommendation")
    val recommendation: String,
)

/**
 * 장소 큐레이션 실패 예외
 */
class PlaceCurationException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
