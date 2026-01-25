package com.dateclick.api.infrastructure.config

import java.net.http.HttpClient
import java.time.Duration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient

@Configuration
class WebClientConfig {

    @Bean
    fun restClientBuilder(): RestClient.Builder {
        val connectTimeout = Duration.ofSeconds(10)
        val readTimeout = Duration.ofSeconds(60)

        val httpClient = HttpClient.newBuilder()
            .connectTimeout(connectTimeout)
            .build()

        val requestFactory = JdkClientHttpRequestFactory(httpClient)
        requestFactory.setReadTimeout(readTimeout)

        return RestClient.builder()
            .requestFactory(requestFactory)
    }

    @Bean
    fun kakaoRestClient(builder: RestClient.Builder): RestClient =
        builder
            .baseUrl("https://dapi.kakao.com")
            .build()

    @Bean
    fun openAiRestClient(builder: RestClient.Builder): RestClient =
        builder
            .baseUrl("https://api.openai.com")
            .build()
}
