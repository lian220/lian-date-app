package com.dateclick.api.domain.place.port.outbound

import com.dateclick.api.domain.place.vo.PlaceWithCuration

/**
 * 장소 메모리 저장 포트
 * Vector DB에 큐레이션된 장소 정보를 저장하고 검색하는 인터페이스
 */
interface PlaceMemoryPort {
    /**
     * 단일 장소를 메모리에 저장
     *
     * @param placeWithCuration 큐레이션 정보가 포함된 장소
     */
    fun addToMemory(placeWithCuration: PlaceWithCuration)

    /**
     * 여러 장소를 배치로 메모리에 저장
     *
     * @param places 큐레이션 정보가 포함된 장소 리스트
     */
    fun addBatchToMemory(places: List<PlaceWithCuration>)

    /**
     * 메모리에서 장소 검색
     *
     * @param query 검색 쿼리 (자연어)
     * @param limit 검색 결과 최대 개수
     * @return 유사도 순으로 정렬된 장소 리스트
     */
    fun searchSimilar(query: String, limit: Int = 10): List<PlaceWithCuration>

    /**
     * 메모리 초기화 (모든 장소 삭제)
     */
    fun clearMemory()

    /**
     * 메모리에 저장된 장소 개수 조회
     *
     * @return 저장된 장소 개수
     */
    fun count(): Long
}
