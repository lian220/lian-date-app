package com.dateclick.api.infrastructure.external.openai

import com.dateclick.api.domain.course.port.outbound.AiCourseRecommendation
import com.dateclick.api.domain.course.port.outbound.AiGenerationPort
import com.dateclick.api.domain.course.port.outbound.AiRecommendedPlace
import com.dateclick.api.domain.course.vo.Budget
import com.dateclick.api.domain.course.vo.DateType
import com.dateclick.api.domain.place.entity.Place
import com.dateclick.api.domain.region.entity.Region
import com.dateclick.api.infrastructure.config.OpenAiProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * OpenAI 기반 AI 코스 생성 어댑터
 * AiGenerationPort 인터페이스의 구현체
 */
@Component
class OpenAiGenerationAdapter(
    private val openAiClient: OpenAiClient,
    private val openAiProperties: OpenAiProperties,
    private val objectMapper: ObjectMapper
) : AiGenerationPort {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun generateCourseRecommendation(
        region: Region,
        dateType: DateType,
        budget: Budget,
        specialRequest: String?,
        candidatePlaces: List<Place>
    ): AiCourseRecommendation {
        logger.info(
            "Generating AI course recommendation for region={}, dateType={}, budget={}, candidatePlaces={}",
            region.name,
            dateType,
            budget.toDisplayName(),
            candidatePlaces.size
        )

        try {
            // 1. 프롬프트 생성
            val messages = PromptBuilder.buildMessages(
                region = region,
                dateType = dateType,
                budget = budget,
                specialRequest = specialRequest,
                candidatePlaces = candidatePlaces
            )

            // 2. OpenAI API 요청 생성
            val request = ChatCompletionRequest(
                model = openAiProperties.model,
                messages = messages,
                temperature = openAiProperties.temperature,
                maxTokens = openAiProperties.maxTokens,
                responseFormat = ResponseFormat(type = "json_object")
            )

            // 3. OpenAI API 호출
            val response = openAiClient.createChatCompletion(request)

            // 4. 응답 파싱 및 변환
            val aiCourseResponse = parseAiResponse(response)

            // 5. Domain 객체로 변환
            val recommendation = AiCourseRecommendation(
                places = aiCourseResponse.places.map { aiPlace ->
                    AiRecommendedPlace(
                        placeId = aiPlace.placeId,
                        order = aiPlace.order,
                        recommendReason = aiPlace.recommendReason,
                        estimatedCost = aiPlace.estimatedCost,
                        estimatedDuration = aiPlace.estimatedDuration,
                        recommendedTime = aiPlace.recommendedTime
                    )
                },
                summary = aiCourseResponse.summary
            )

            logger.info("Successfully generated course recommendation with {} places", recommendation.places.size)
            return recommendation

        } catch (ex: OpenAiException) {
            logger.error("Failed to generate course recommendation due to OpenAI API error", ex)
            throw AiGenerationException("AI 코스 생성 중 OpenAI API 오류가 발생했습니다: ${ex.message}", ex)
        } catch (ex: Exception) {
            logger.error("Unexpected error while generating course recommendation", ex)
            throw AiGenerationException("AI 코스 생성 중 예상치 못한 오류가 발생했습니다: ${ex.message}", ex)
        }
    }

    /**
     * OpenAI 응답을 파싱하여 AiCourseResponse로 변환
     */
    private fun parseAiResponse(response: ChatCompletionResponse): AiCourseResponse {
        if (response.choices.isEmpty()) {
            throw AiGenerationException("OpenAI 응답에 선택지가 없습니다")
        }

        val messageContent = response.choices.first().message.content
        logger.debug("OpenAI response content: {}", messageContent)

        return try {
            objectMapper.readValue<AiCourseResponse>(messageContent)
        } catch (ex: Exception) {
            logger.error("Failed to parse OpenAI response: {}", messageContent, ex)
            throw AiGenerationException("OpenAI 응답 파싱 실패: ${ex.message}", ex)
        }
    }
}

/**
 * AI 코스 생성 실패 예외
 */
class AiGenerationException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
