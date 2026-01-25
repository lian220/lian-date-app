package com.dateclick.api.domain.place.vo

/**
 * AI 큐레이션 정보
 * OpenAI API를 통해 분석된 장소의 데이트 적합도 및 메타데이터
 */
data class PlaceCurationInfo(
    /**
     * 데이트 적합도 점수 (1-10점)
     * 10점에 가까울수록 데이트하기 좋은 장소
     */
    val dateScore: Int,

    /**
     * 분위기 태그 (최대 3개)
     * 예: #로맨틱, #아늑한, #트렌디
     */
    val moodTags: List<String>,

    /**
     * 1인당 예상 가격대 (원)
     */
    val priceRange: Int,

    /**
     * 추천 시간대
     * 예: "오후 2-5시", "저녁 7-9시"
     */
    val bestTime: String,

    /**
     * 한 줄 추천 이유
     */
    val recommendation: String
) {
    init {
        require(dateScore in 1..10) { "dateScore must be between 1 and 10" }
        require(moodTags.size <= 3) { "moodTags must have at most 3 tags" }
        require(priceRange >= 0) { "priceRange must be non-negative" }
    }
}
