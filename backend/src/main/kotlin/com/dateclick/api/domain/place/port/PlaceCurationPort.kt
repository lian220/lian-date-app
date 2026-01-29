package com.dateclick.api.domain.place.port

import com.dateclick.api.domain.place.entity.Place
import com.dateclick.api.domain.place.vo.PlaceCurationInfo

/**
 * 장소 큐레이션 포트
 * AI 기반으로 장소의 데이트 적합도를 분석하는 인터페이스
 */
interface PlaceCurationPort {
    /**
     * 장소 기본 정보를 기반으로 AI 큐레이션 수행
     *
     * @param place 큐레이션할 장소 기본 정보
     * @return AI가 분석한 큐레이션 정보
     */
    fun curatePlace(place: Place): PlaceCurationInfo
}
