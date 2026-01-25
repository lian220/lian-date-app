package com.dateclick.api.infrastructure.external.openai

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * OpenAI Chat Completion API 응답 DTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Choice(
    val index: Int,
    val message: Message,
    @JsonProperty("finish_reason")
    val finishReason: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Usage(
    @JsonProperty("prompt_tokens")
    val promptTokens: Int,
    @JsonProperty("completion_tokens")
    val completionTokens: Int,
    @JsonProperty("total_tokens")
    val totalTokens: Int
)

/**
 * AI 코스 추천 응답 (GPT-4가 생성하는 JSON 형식)
 * MVP: AI가 장소 정보를 포함한 전체 코스를 생성
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class AiCourseResponse(
    val places: List<AiPlaceRecommendation>,
    val summary: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AiPlaceRecommendation(
    val order: Int,
    val name: String,
    val category: String,
    @JsonProperty("category_detail")
    val categoryDetail: String?,
    val address: String,
    @JsonProperty("road_address")
    val roadAddress: String?,
    val location: LocationDto,
    val phone: String?,
    @JsonProperty("recommend_reason")
    val recommendReason: String,
    @JsonProperty("estimated_cost")
    val estimatedCost: Int,
    @JsonProperty("estimated_duration")
    val estimatedDuration: Int,
    @JsonProperty("recommended_time")
    val recommendedTime: String?
)

data class LocationDto(
    val lat: Double,
    val lng: Double
)
