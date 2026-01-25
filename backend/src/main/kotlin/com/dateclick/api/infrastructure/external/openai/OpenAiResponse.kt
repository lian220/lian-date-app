package com.dateclick.api.infrastructure.external.openai

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * OpenAI Chat Completion API 응답 DTO
 */
data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
)

data class Choice(
    val index: Int,
    val message: Message,
    @JsonProperty("finish_reason")
    val finishReason: String
)

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
 */
data class AiCourseResponse(
    val places: List<AiPlaceRecommendation>,
    val summary: String?
)

data class AiPlaceRecommendation(
    @JsonProperty("place_id")
    val placeId: String,
    val order: Int,
    @JsonProperty("recommend_reason")
    val recommendReason: String,
    @JsonProperty("estimated_cost")
    val estimatedCost: Int,
    @JsonProperty("estimated_duration")
    val estimatedDuration: Int,
    @JsonProperty("recommended_time")
    val recommendedTime: String?
)
