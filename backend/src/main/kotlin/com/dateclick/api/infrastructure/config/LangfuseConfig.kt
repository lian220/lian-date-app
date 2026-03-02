package com.dateclick.api.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(LangfuseProperties::class)
class LangfuseConfig

@ConfigurationProperties(prefix = "langfuse")
data class LangfuseProperties(
    val publicKey: String = "",
    val secretKey: String = "",
    val host: String = "https://us.cloud.langfuse.com",
    val enabled: Boolean = true,
)
