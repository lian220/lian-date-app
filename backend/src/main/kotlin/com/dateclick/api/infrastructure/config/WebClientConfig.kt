package com.dateclick.api.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun webClientBuilder(): WebClient.Builder = WebClient.builder()

    @Bean
    fun kakaoWebClient(builder: WebClient.Builder): WebClient =
        builder
            .baseUrl("https://dapi.kakao.com")
            .build()

    @Bean
    fun openAiWebClient(builder: WebClient.Builder): WebClient =
        builder
            .baseUrl("https://api.openai.com")
            .build()
}
