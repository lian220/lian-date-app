package com.dateclick.api.application.place

import com.dateclick.api.domain.course.port.outbound.PlaceSearchPort
import com.dateclick.api.domain.place.entity.Place
import com.dateclick.api.domain.place.vo.PlaceId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 장소 상세 조회 Use Case 구현체
 */
@Service
class GetPlaceDetailUseCaseImpl(
    private val placeSearchPort: PlaceSearchPort
) : GetPlaceDetailUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun execute(placeId: PlaceId): Place? {
        logger.info("Getting place detail: {}", placeId.value)

        // 카카오 API로 장소 상세 정보 조회
        val place = placeSearchPort.getPlaceDetail(placeId)

        if (place != null) {
            logger.info("Place detail found: name={}, category={}", place.name, place.category)
        } else {
            logger.warn("Place not found: {}", placeId.value)
        }

        return place
    }
}
