package com.dateclick.api.infrastructure.external.langfuse

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class LangfuseTracer(
    private val langfuseIngestionClient: LangfuseIngestionClient,
) : DisposableBean {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun destroy() {
        scope.cancel()
    }

    fun recordGeneration(
        traceName: String,
        model: String,
        input: Any,
        output: String?,
        promptTokens: Int,
        completionTokens: Int,
        totalTokens: Int,
        startTime: Instant,
        endTime: Instant,
        sessionId: String? = null,
    ) {
        scope.launch {
            try {
                val traceId = UUID.randomUUID().toString()
                val generationId = UUID.randomUUID().toString()
                val now = Instant.now().toString()

                val traceBody =
                    buildMap<String, Any?> {
                        put("id", traceId)
                        put("name", traceName)
                        if (sessionId != null) put("sessionId", sessionId)
                    }

                val events =
                    listOf(
                        LangfuseEvent(
                            id = UUID.randomUUID().toString(),
                            timestamp = now,
                            type = "trace-create",
                            body = traceBody,
                        ),
                        LangfuseEvent(
                            id = UUID.randomUUID().toString(),
                            timestamp = now,
                            type = "generation-create",
                            body =
                                mapOf(
                                    "id" to generationId,
                                    "traceId" to traceId,
                                    "name" to traceName,
                                    "model" to model,
                                    "startTime" to startTime.toString(),
                                    "endTime" to endTime.toString(),
                                    "input" to input,
                                    "output" to output,
                                    "usage" to
                                        mapOf(
                                            "input" to promptTokens,
                                            "output" to completionTokens,
                                            "total" to totalTokens,
                                        ),
                                ),
                        ),
                    )

                langfuseIngestionClient.ingest(LangfuseIngestionRequest(batch = events))
            } catch (ex: Exception) {
                logger.warn("Failed to send trace to Langfuse: {}", ex.message)
            }
        }
    }
}
