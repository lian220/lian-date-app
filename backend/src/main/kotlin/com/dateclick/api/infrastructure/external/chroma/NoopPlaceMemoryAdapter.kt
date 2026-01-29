package com.dateclick.api.infrastructure.external.chroma

import com.dateclick.api.domain.place.port.outbound.PlaceMemoryPort
import com.dateclick.api.domain.place.vo.PlaceWithCuration
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Chroma가 배포 환경에 구성되지 않았을 때 사용하는 No-op 구현체.
 *
 * - chroma.enabled=false 인 경우 활성화됩니다.
 * - 검색은 항상 빈 결과를 반환하고, 저장/삭제는 수행하지 않습니다.
 */
@Component
@ConditionalOnProperty(prefix = "chroma", name = ["enabled"], havingValue = "false")
class NoopPlaceMemoryAdapter : PlaceMemoryPort {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun addToMemory(placeWithCuration: PlaceWithCuration) {
        logger.debug("Chroma disabled. Skipping addToMemory(placeId={})", placeWithCuration.place.id.value)
    }

    override fun addBatchToMemory(places: List<PlaceWithCuration>) {
        logger.debug("Chroma disabled. Skipping addBatchToMemory(size={})", places.size)
    }

    override fun searchSimilar(
        query: String,
        limit: Int,
    ): List<PlaceWithCuration> {
        logger.debug("Chroma disabled. Returning empty search result (query='{}', limit={})", query, limit)
        return emptyList()
    }

    override fun clearMemory() {
        logger.debug("Chroma disabled. Skipping clearMemory()")
    }

    override fun count(): Long = 0L
}

