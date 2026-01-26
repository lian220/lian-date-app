package com.dateclick.api.application.place

import com.dateclick.api.domain.course.port.outbound.PlaceSearchPort
import com.dateclick.api.domain.place.port.PlaceCurationPort
import com.dateclick.api.domain.place.port.outbound.PlaceMemoryPort
import com.dateclick.api.domain.place.vo.PlaceId
import com.dateclick.api.domain.place.vo.PlaceWithCuration
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 장소 메모리 관리 서비스
 * AI 큐레이션된 장소 정보를 Vector DB에 저장하여 의미 기반 검색 가능하게 함
 */
@Service
class PlaceMemoryService(
    private val placeSearchPort: PlaceSearchPort,
    private val placeCurationPort: PlaceCurationPort,
    private val placeMemoryPort: PlaceMemoryPort,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 장소 리스트를 큐레이션하여 Vector DB에 저장
     *
     * @param placeIds 저장할 장소 ID 리스트
     * @return 저장된 장소 개수
     */
    fun buildPlaceMemory(placeIds: List<PlaceId>): Int {
        if (placeIds.isEmpty()) {
            logger.warn("No place IDs provided for memory building")
            return 0
        }

        logger.info("Starting to build place memory for {} places", placeIds.size)

        try {
            // 1단계: 장소 정보 수집
            logger.info("Step 1: Collecting place information from Kakao API")
            val places = collectPlaces(placeIds)
            logger.info("Collected {} places", places.size)

            if (places.isEmpty()) {
                logger.warn("No valid places found")
                return 0
            }

            // 2단계: AI 큐레이션 수행
            logger.info("Step 2: Curating places with AI")
            val curatedPlaces = curatePlaces(places)
            logger.info("Curated {} places", curatedPlaces.size)

            if (curatedPlaces.isEmpty()) {
                logger.warn("No places successfully curated")
                return 0
            }

            // 3단계: Vector DB에 배치 저장 (10개씩)
            logger.info("Step 3: Saving curated places to Vector DB")
            saveToBatchMemory(curatedPlaces)

            logger.info("Successfully built place memory for {} places", curatedPlaces.size)
            return curatedPlaces.size
        } catch (ex: Exception) {
            logger.error("Failed to build place memory", ex)
            throw PlaceMemoryException("장소 메모리 구축 실패: ${ex.message}", ex)
        }
    }

    /**
     * 장소 정보 수집 (카카오 API)
     */
    private fun collectPlaces(placeIds: List<PlaceId>) =
        placeIds.mapNotNull { placeId ->
            try {
                placeSearchPort.getPlaceDetail(placeId)?.also {
                    logger.debug("Collected place: {}", it.name)
                }
            } catch (ex: Exception) {
                logger.warn("Failed to collect place: {}", placeId.value, ex)
                null
            }
        }

    /**
     * AI 큐레이션 수행
     */
    private fun curatePlaces(places: List<com.dateclick.api.domain.place.entity.Place>) =
        places.mapNotNull { place ->
            try {
                val curation = placeCurationPort.curatePlace(place)
                PlaceWithCuration(place, curation).also {
                    logger.debug(
                        "Curated place: {} (score: {}/10)",
                        place.name,
                        curation.dateScore,
                    )
                }
            } catch (ex: Exception) {
                logger.warn("Failed to curate place: {}", place.name, ex)
                null
            }
        }

    /**
     * Vector DB에 배치 저장
     * 10개씩 끊어서 저장하며 진행 상황 로깅
     */
    private fun saveToBatchMemory(curatedPlaces: List<PlaceWithCuration>) {
        val totalBatches = (curatedPlaces.size + 9) / 10 // 올림 계산

        logger.info(
            "Saving {} places in {} batches (10 places per batch)",
            curatedPlaces.size,
            totalBatches,
        )

        try {
            placeMemoryPort.addBatchToMemory(curatedPlaces)

            val totalCount = placeMemoryPort.count()
            logger.info("Memory save completed. Total places in memory: {}", totalCount)
        } catch (ex: Exception) {
            logger.error("Failed to save places to memory", ex)
            throw PlaceMemoryException("Vector DB 저장 실패: ${ex.message}", ex)
        }
    }

    /**
     * 의미 기반 장소 검색
     *
     * @param query 검색 쿼리 (자연어)
     * @param limit 검색 결과 최대 개수
     * @return 유사도 순으로 정렬된 장소 리스트
     */
    fun searchPlaces(
        query: String,
        limit: Int = 10,
    ): List<PlaceWithCuration> {
        logger.info("Searching places with query: '{}' (limit: {})", query, limit)

        return try {
            val results = placeMemoryPort.searchSimilar(query, limit)
            logger.info("Found {} matching places", results.size)
            results
        } catch (ex: Exception) {
            logger.error("Failed to search places", ex)
            emptyList()
        }
    }

    /**
     * 저장된 장소 개수 조회
     */
    fun getMemoryCount(): Long {
        return placeMemoryPort.count()
    }

    /**
     * 메모리 초기화
     */
    fun clearMemory() {
        logger.warn("Clearing all place memory")
        placeMemoryPort.clearMemory()
    }
}

/**
 * 장소 메모리 관련 예외
 */
class PlaceMemoryException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
