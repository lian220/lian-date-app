package com.dateclick.api.domain.place.vo

import com.dateclick.api.domain.place.entity.Place

/**
 * 큐레이션 정보가 포함된 장소
 * AI 분석이 완료된 장소 정보와 큐레이션 결과를 함께 보관
 */
data class PlaceWithCuration(
    /**
     * 장소 기본 정보
     */
    val place: Place,

    /**
     * AI 큐레이션 정보
     */
    val curation: PlaceCurationInfo
) {
    /**
     * Vector DB 저장을 위한 자연어 문서로 변환
     * AI가 의미를 파악할 수 있도록 상세하고 구조화된 텍스트 생성
     */
    fun toDocument(): String {
        return buildString {
            appendLine("# ${place.name}")
            appendLine()

            // 기본 정보
            appendLine("## 기본 정보")
            appendLine("- 장소명: ${place.name}")
            appendLine("- 카테고리: ${place.category}")
            place.categoryDetail?.let { appendLine("- 상세 카테고리: $it") }
            appendLine("- 주소: ${place.address}")
            place.roadAddress?.let { appendLine("- 도로명 주소: $it") }
            appendLine("- 위치: (위도: ${place.location.lat}, 경도: ${place.location.lng})")
            place.phone?.let { appendLine("- 전화번호: $it") }
            appendLine()

            // 영업 정보
            place.businessHours?.let { hours ->
                if (hours.isNotEmpty()) {
                    appendLine("## 영업 정보")
                    hours.forEach { hour ->
                        if (hour.isClosed) {
                            appendLine("- ${hour.day}: 휴무")
                        } else {
                            appendLine("- ${hour.day}: ${hour.open} ~ ${hour.close}")
                        }
                    }
                    appendLine()
                }
            }

            // 평점 정보
            if (place.kakaoRating != null || place.kakaoReviewCount != null) {
                appendLine("## 평가")
                place.kakaoRating?.let { appendLine("- 카카오맵 평점: ${"%.1f".format(it)}점") }
                place.kakaoReviewCount?.let { appendLine("- 리뷰 수: ${it}개") }
                appendLine()
            }

            // AI 큐레이션 정보
            appendLine("## 데이트 큐레이션")
            appendLine("- 데이트 적합도: ${curation.dateScore}/10점")
            appendLine("- 분위기: ${curation.moodTags.joinToString(", ")}")
            appendLine("- 1인당 예상 가격: ${"%,d".format(curation.priceRange)}원")
            appendLine("- 추천 시간대: ${curation.bestTime}")
            appendLine("- 추천 이유: ${curation.recommendation}")
        }
    }

    /**
     * 간단한 요약 정보
     */
    fun getSummary(): String {
        return "${place.name} (${curation.moodTags.joinToString(", ")}) - " +
                "데이트 점수: ${curation.dateScore}/10, " +
                "가격대: ${"%,d".format(curation.priceRange)}원"
    }
}
