package com.dateclick.api.infrastructure.external.langfuse

import com.dateclick.api.infrastructure.config.LangfuseProperties
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.util.Base64

@Component
class LangfuseIngestionClient(
    private val langfuseRestClient: RestClient,
    private val langfuseProperties: LangfuseProperties,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val authHeader: String by lazy {
        val credentials = "${langfuseProperties.publicKey}:${langfuseProperties.secretKey}"
        "Basic ${Base64.getEncoder().encodeToString(credentials.toByteArray())}"
    }

    fun ingest(request: LangfuseIngestionRequest) {
        if (!langfuseProperties.enabled) return
        if (langfuseProperties.publicKey.isBlank() || langfuseProperties.secretKey.isBlank()) return

        langfuseRestClient
            .post()
            .uri("/api/public/ingestion")
            .header("Authorization", authHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toBodilessEntity()

        logger.debug("Sent {} events to Langfuse", request.batch.size)
    }
}
