package com.dateclick.api.application.place

import com.dateclick.api.domain.course.port.outbound.PlaceSearchPort
import com.dateclick.api.domain.place.port.PlaceCurationPort
import com.dateclick.api.domain.place.vo.PlaceCurationInfo
import com.dateclick.api.domain.place.vo.PlaceId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 장소 큐레이션 Use Case 구현체
 */
@Service
class CuratePlaceUseCaseImpl(
    private val placeSearchPort: PlaceSearchPort,
    private val placeCurationPort: PlaceCurationPort
) : CuratePlaceUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun execute(placeId: PlaceId): PlaceCurationInfo {
        logger.info("Curating place: {}", placeId.value)

        // 1. 카카오 API로 장소 상세 정보 조회
        val place = placeSearchPort.getPlaceDetail(placeId)
            ?: throw PlaceNotFoundException("장소를 찾을 수 없습니다: ${placeId.value}")

        // 2. OpenAI API로 큐레이션 수행
        val curationInfo = placeCurationPort.curatePlace(place)

        logger.info(
            "Place curation completed: name={}, dateScore={}",
            place.name,
            curationInfo.dateScore
        )

        return curationInfo
    }
}

/**
 * 장소를 찾을 수 없을 때 발생하는 예외
 */
class PlaceNotFoundException(message: String) : RuntimeException(message)
