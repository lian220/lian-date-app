package com.dateclick.api.infrastructure.external.openai

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * OpenAI Chat Completion API 요청 DTO
 */
data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.7,
    @JsonProperty("max_tokens")
    val maxTokens: Int = 2000,
    @JsonProperty("response_format")
    val responseFormat: ResponseFormat? = null
)

data class Message(
    val role: String,
    val content: String
) {
    companion object {
        fun system(content: String) = Message("system", content)
        fun user(content: String) = Message("user", content)
        fun assistant(content: String) = Message("assistant", content)
    }
}

data class ResponseFormat(
    val type: String = "json_object"
)
