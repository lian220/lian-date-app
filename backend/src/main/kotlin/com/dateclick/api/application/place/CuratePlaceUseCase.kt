package com.dateclick.api.application.place

import com.dateclick.api.domain.place.vo.PlaceCurationInfo
import com.dateclick.api.domain.place.vo.PlaceId

/**
 * 장소 큐레이션 Use Case 인터페이스
 */
interface CuratePlaceUseCase {
    /**
     * 장소 ID로 큐레이션 정보 조회
     *
     * @param placeId 큐레이션할 장소 ID
     * @return AI가 분석한 큐레이션 정보
     */
    fun execute(placeId: PlaceId): PlaceCurationInfo
}
