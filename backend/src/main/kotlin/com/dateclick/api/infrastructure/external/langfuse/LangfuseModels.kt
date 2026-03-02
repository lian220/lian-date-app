package com.dateclick.api.infrastructure.external.langfuse

data class LangfuseIngestionRequest(
    val batch: List<LangfuseEvent>,
)

data class LangfuseEvent(
    val id: String,
    val timestamp: String,
    val type: String,
    val body: Map<String, Any?>,
)
