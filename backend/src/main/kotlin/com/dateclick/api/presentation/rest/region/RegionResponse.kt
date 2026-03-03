package com.dateclick.api.presentation.rest.region

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "지역 목록 응답")
data class RegionListResponse(
    @Schema(description = "지역 목록")
    val regions: List<RegionResponse>,
)

@Schema(description = "지역 정보")
data class RegionResponse(
    @Schema(description = "지역 ID", example = "region-hongdae")
    val id: String,
    @Schema(description = "지역명", example = "홍대")
    val name: String,
    @Schema(description = "도시명", example = "서울")
    val city: String,
    @Schema(description = "지역 설명", example = "젊음과 예술의 거리, 다양한 카페와 음식점")
    val description: String,
    @Schema(description = "지역 키워드", example = "[\"감성카페\", \"인디문화\", \"야간명소\"]")
    val keywords: List<String>,
    @Schema(description = "중심 위도", example = "37.5519")
    val centerLat: Double,
    @Schema(description = "중심 경도", example = "126.9244")
    val centerLng: Double,
)
