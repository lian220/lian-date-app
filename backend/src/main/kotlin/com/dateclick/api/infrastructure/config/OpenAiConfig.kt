package com.dateclick.api.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(OpenAiProperties::class)
class OpenAiConfig

@ConfigurationProperties(prefix = "openai")
data class OpenAiProperties(
    val apiKey: String = "",
    val model: String = "gpt-4",
    val maxTokens: Int = 2000,
    val temperature: Double = 0.7,
)
