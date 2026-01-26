package com.dateclick.api.infrastructure.external.chroma

import com.dateclick.api.infrastructure.config.ChromaDbProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

/**
 * Chroma DB REST API 클라이언트
 */
@Component
@ConditionalOnProperty(prefix = "chroma", name = ["enabled"], havingValue = "true", matchIfMissing = true)
class ChromaDbClient(
    private val chromaDbProperties: ChromaDbProperties,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val restClient =
        RestClient.builder()
            .baseUrl(chromaDbProperties.url)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

    /**
     * 컬렉션 생성 또는 가져오기
     */
    fun getOrCreateCollection(
        name: String,
        metadata: Map<String, Any> = emptyMap(),
    ): ChromaCollection {
        logger.info("Getting or creating collection: {}", name)

        return try {
            val request =
                CreateCollectionRequest(
                    name = name,
                    metadata = metadata.takeIf { it.isNotEmpty() },
                )

            val response =
                restClient.post()
                    .uri("/api/v1/collections")
                    .body(request)
                    .retrieve()
                    .body<ChromaCollection>()

            logger.info("Collection ready: {}", name)
            response ?: throw ChromaException("Failed to create/get collection: $name")
        } catch (ex: Exception) {
            logger.error("Failed to get or create collection: {}", name, ex)
            throw ChromaException("Failed to get or create collection: ${ex.message}", ex)
        }
    }

    /**
     * 컬렉션에 문서 추가
     */
    fun addDocuments(
        collectionName: String,
        documents: List<String>,
        metadatas: List<Map<String, Any>>? = null,
        ids: List<String>? = null,
    ) {
        logger.debug("Adding {} documents to collection: {}", documents.size, collectionName)

        try {
            val request =
                AddDocumentsRequest(
                    documents = documents,
                    metadatas = metadatas,
                    ids = ids,
                )

            restClient.post()
                .uri("/api/v1/collections/$collectionName/add")
                .body(request)
                .retrieve()
                .toBodilessEntity()

            logger.info("Successfully added {} documents to {}", documents.size, collectionName)
        } catch (ex: Exception) {
            logger.error("Failed to add documents to collection: {}", collectionName, ex)
            throw ChromaException("Failed to add documents: ${ex.message}", ex)
        }
    }

    /**
     * 유사 문서 검색
     */
    fun query(
        collectionName: String,
        queryTexts: List<String>,
        nResults: Int = 10,
        where: Map<String, Any>? = null,
    ): QueryResponse {
        logger.debug("Querying collection: {} with {} queries", collectionName, queryTexts.size)

        return try {
            val request =
                QueryRequest(
                    queryTexts = queryTexts,
                    nResults = nResults,
                    where = where,
                )

            val response =
                restClient.post()
                    .uri("/api/v1/collections/$collectionName/query")
                    .body(request)
                    .retrieve()
                    .body<QueryResponse>()

            logger.debug("Query completed with {} results", response?.ids?.firstOrNull()?.size ?: 0)
            response ?: throw ChromaException("Failed to query collection: $collectionName")
        } catch (ex: Exception) {
            logger.error("Failed to query collection: {}", collectionName, ex)
            throw ChromaException("Failed to query collection: ${ex.message}", ex)
        }
    }

    /**
     * 컬렉션 삭제
     */
    fun deleteCollection(name: String) {
        logger.info("Deleting collection: {}", name)

        try {
            restClient.delete()
                .uri("/api/v1/collections/$name")
                .retrieve()
                .toBodilessEntity()

            logger.info("Collection deleted: {}", name)
        } catch (ex: Exception) {
            logger.error("Failed to delete collection: {}", name, ex)
            throw ChromaException("Failed to delete collection: ${ex.message}", ex)
        }
    }

    /**
     * 컬렉션 문서 개수 조회
     */
    fun count(collectionName: String): Long {
        logger.debug("Getting count for collection: {}", collectionName)

        return try {
            val response =
                restClient.get()
                    .uri("/api/v1/collections/$collectionName/count")
                    .retrieve()
                    .body<Long>()

            response ?: 0L
        } catch (ex: Exception) {
            logger.error("Failed to get count for collection: {}", collectionName, ex)
            0L
        }
    }
}

// Request/Response DTOs
data class CreateCollectionRequest(
    val name: String,
    val metadata: Map<String, Any>? = null,
)

data class ChromaCollection(
    val name: String,
    val id: String,
    val metadata: Map<String, Any>? = null,
)

data class AddDocumentsRequest(
    val documents: List<String>,
    val metadatas: List<Map<String, Any>>? = null,
    val ids: List<String>? = null,
)

data class QueryRequest(
    @JsonProperty("query_texts")
    val queryTexts: List<String>,
    @JsonProperty("n_results")
    val nResults: Int = 10,
    val where: Map<String, Any>? = null,
)

data class QueryResponse(
    val ids: List<List<String>>,
    val distances: List<List<Double>>,
    val documents: List<List<String>>,
    val metadatas: List<List<Map<String, Any>>>,
)

/**
 * Chroma DB 관련 예외
 */
class ChromaException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
