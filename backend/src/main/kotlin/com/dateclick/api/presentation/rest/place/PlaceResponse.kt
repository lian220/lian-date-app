package com.dateclick.api.presentation.rest.place

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 장소 큐레이션 응답 DTO
 */
@Schema(description = "장소 큐레이션 정보")
data class PlaceCurationResponse(
    @Schema(description = "데이트 적합도 점수 (1-10점)", example = "8")
    val dateScore: Int,

    @Schema(description = "분위기 태그 (최대 3개)", example = "[\"#로맨틱\", \"#아늑한\", \"#트렌디\"]")
    val moodTags: List<String>,

    @Schema(description = "1인당 예상 가격대 (원)", example = "25000")
    val priceRange: Int,

    @Schema(description = "추천 시간대", example = "저녁 7-9시")
    val bestTime: String,

    @Schema(description = "한 줄 추천 이유", example = "분위기 좋은 루프탑에서 야경을 즐기며 데이트하기 좋은 곳")
    val recommendation: String
)
