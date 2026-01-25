package com.dateclick.api.infrastructure.external.chroma

import com.dateclick.api.domain.place.entity.Place
import com.dateclick.api.domain.place.port.outbound.PlaceMemoryPort
import com.dateclick.api.domain.place.vo.*
import com.dateclick.api.infrastructure.config.ChromaDbProperties
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*
import jakarta.annotation.PostConstruct

/**
 * Chroma DB 기반 장소 메모리 어댑터
 */
@Component
class ChromaPlaceMemoryAdapter(
    private val chromaDbClient: ChromaDbClient,
    private val chromaDbProperties: ChromaDbProperties,
    private val objectMapper: ObjectMapper
) : PlaceMemoryPort {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val collectionName = chromaDbProperties.placeCollectionName

    @PostConstruct
    fun initialize() {
        logger.info("Initializing Chroma Place Memory with collection: {}", collectionName)
        try {
            chromaDbClient.getOrCreateCollection(
                name = collectionName,
                metadata = mapOf("description" to "Date place curation memory")
            )
            logger.info("Chroma Place Memory initialized successfully")
        } catch (ex: Exception) {
            logger.error("Failed to initialize Chroma Place Memory", ex)
        }
    }

    override fun addToMemory(placeWithCuration: PlaceWithCuration) {
        logger.debug("Adding single place to memory: {}", placeWithCuration.place.name)
        addBatchToMemory(listOf(placeWithCuration))
    }

    override fun addBatchToMemory(places: List<PlaceWithCuration>) {
        if (places.isEmpty()) {
            logger.debug("No places to add to memory")
            return
        }

        logger.info("Adding {} places to Chroma memory in batches of {}",
            places.size, chromaDbProperties.batchSize)

        try {
            // 배치 단위로 나누어 저장
            places.chunked(chromaDbProperties.batchSize).forEachIndexed { index, batch ->
                logger.debug("Processing batch {}/{}", index + 1,
                    (places.size + chromaDbProperties.batchSize - 1) / chromaDbProperties.batchSize)

                val documents = batch.map { it.toDocument() }
                val metadatas = batch.map { toMetadata(it) }
                val ids = batch.map { it.place.id.value }

                chromaDbClient.addDocuments(
                    collectionName = collectionName,
                    documents = documents,
                    metadatas = metadatas,
                    ids = ids
                )

                logger.info("Batch {} saved: {} places", index + 1, batch.size)
            }

            logger.info("Successfully added all {} places to memory", places.size)

        } catch (ex: Exception) {
            logger.error("Failed to add places to memory", ex)
            throw ChromaException("Failed to add places to memory: ${ex.message}", ex)
        }
    }

    override fun searchSimilar(query: String, limit: Int): List<PlaceWithCuration> {
        logger.debug("Searching similar places with query: {}", query)

        return try {
            val response = chromaDbClient.query(
                collectionName = collectionName,
                queryTexts = listOf(query),
                nResults = limit
            )

            if (response.ids.isEmpty() || response.ids[0].isEmpty()) {
                logger.debug("No results found for query: {}", query)
                return emptyList()
            }

            val results = response.ids[0].zip(response.metadatas[0]).map { (id, metadata) ->
                fromMetadata(id, metadata)
            }

            logger.info("Found {} similar places for query", results.size)
            results

        } catch (ex: Exception) {
            logger.error("Failed to search similar places", ex)
            emptyList()
        }
    }

    override fun clearMemory() {
        logger.warn("Clearing all place memory")

        try {
            chromaDbClient.deleteCollection(collectionName)
            // 컬렉션 재생성
            chromaDbClient.getOrCreateCollection(
                name = collectionName,
                metadata = mapOf("description" to "Date place curation memory")
            )
            logger.info("Place memory cleared successfully")

        } catch (ex: Exception) {
            logger.error("Failed to clear place memory", ex)
            throw ChromaException("Failed to clear place memory: ${ex.message}", ex)
        }
    }

    override fun count(): Long {
        return try {
            val count = chromaDbClient.count(collectionName)
            logger.debug("Place memory count: {}", count)
            count
        } catch (ex: Exception) {
            logger.error("Failed to get place memory count", ex)
            0L
        }
    }

    /**
     * PlaceWithCuration을 Chroma 메타데이터로 변환
     */
    private fun toMetadata(placeWithCuration: PlaceWithCuration): Map<String, Any> {
        val place = placeWithCuration.place
        val curation = placeWithCuration.curation

        return buildMap {
            put("place_id", place.id.value)
            put("name", place.name)
            put("category", place.category)
            place.categoryDetail?.let { put("category_detail", it) }
            put("address", place.address)
            place.roadAddress?.let { put("road_address", it) }
            place.phone?.let { put("phone", it) }
            put("latitude", place.location.lat)
            put("longitude", place.location.lng)
            place.kakaoRating?.let { put("kakao_rating", it) }
            place.kakaoReviewCount?.let { put("kakao_review_count", it) }

            // 큐레이션 정보
            put("date_score", curation.dateScore)
            put("mood_tags", curation.moodTags.joinToString(","))
            put("price_range", curation.priceRange)
            put("best_time", curation.bestTime)
            put("recommendation", curation.recommendation)
        }
    }

    /**
     * Chroma 메타데이터를 PlaceWithCuration으로 변환
     */
    private fun fromMetadata(id: String, metadata: Map<String, Any>): PlaceWithCuration {
        val place = Place(
            id = PlaceId(id),
            name = metadata["name"] as String,
            category = metadata["category"] as String,
            categoryDetail = metadata["category_detail"] as? String,
            address = metadata["address"] as String,
            roadAddress = metadata["road_address"] as? String,
            phone = metadata["phone"] as? String,
            location = Location(
                lat = (metadata["latitude"] as Number).toDouble(),
                lng = (metadata["longitude"] as Number).toDouble()
            ),
            kakaoRating = (metadata["kakao_rating"] as? Number)?.toDouble(),
            kakaoReviewCount = (metadata["kakao_review_count"] as? Number)?.toInt()
        )

        val curation = PlaceCurationInfo(
            dateScore = (metadata["date_score"] as Number).toInt(),
            moodTags = (metadata["mood_tags"] as String).split(","),
            priceRange = (metadata["price_range"] as Number).toInt(),
            bestTime = metadata["best_time"] as String,
            recommendation = metadata["recommendation"] as String
        )

        return PlaceWithCuration(place, curation)
    }
}
