package com.dateclick.api.presentation.rest.course

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "데이트 코스 응답")
data class CourseResponse(
    @Schema(description = "코스 ID", example = "course-abc123")
    val courseId: String,
    @Schema(description = "지역 ID", example = "region-hongdae")
    val regionId: String,
    @Schema(description = "지역명", example = "홍대")
    val regionName: String,
    @Schema(description = "데이트 유형", example = "ROMANTIC")
    val dateType: String,
    @Schema(description = "예산", example = "MEDIUM")
    val budget: String,
    @Schema(description = "총 예상 비용 (원)", example = "80000")
    val totalEstimatedCost: Int,
    @Schema(description = "코스 장소 목록")
    val places: List<CoursePlaceResponse>,
    @Schema(description = "이동 경로 목록")
    val routes: List<RouteResponse>,
    @Schema(description = "코스 생성 시각")
    val createdAt: Instant,
)

@Schema(description = "코스 내 장소 정보")
data class CoursePlaceResponse(
    @Schema(description = "방문 순서", example = "1")
    val order: Int,
    @Schema(description = "장소 ID", example = "12345")
    val placeId: String,
    @Schema(description = "장소명", example = "카페 온더문")
    val name: String,
    @Schema(description = "카테고리", example = "카페")
    val category: String,
    @Schema(description = "상세 카테고리", example = "카페 > 디저트카페", nullable = true)
    val categoryDetail: String?,
    @Schema(description = "주소", example = "서울 마포구 연남동 123-45")
    val address: String,
    @Schema(description = "도로명 주소", nullable = true)
    val roadAddress: String?,
    @Schema(description = "위도", example = "37.5665")
    val lat: Double,
    @Schema(description = "경도", example = "126.9780")
    val lng: Double,
    @Schema(description = "전화번호", nullable = true)
    val phone: String?,
    @Schema(description = "예상 비용 (원)", example = "25000")
    val estimatedCost: Int,
    @Schema(description = "예상 소요 시간 (분)", example = "90")
    val estimatedDuration: Int,
    @Schema(description = "추천 방문 시간대", example = "저녁 7-9시", nullable = true)
    val recommendedTime: String?,
    @Schema(description = "추천 이유", example = "분위기 좋은 루프탑에서 야경을 즐기기 좋은 곳")
    val recommendReason: String,
    @Schema(description = "이미지 URL", nullable = true)
    val imageUrl: String?,
    @Schema(description = "카카오맵 URL", nullable = true)
    val kakaoPlaceUrl: String?,
)

@Schema(description = "장소 간 이동 경로")
data class RouteResponse(
    @Schema(description = "출발 장소 순서", example = "1")
    val from: Int,
    @Schema(description = "도착 장소 순서", example = "2")
    val to: Int,
    @Schema(description = "이동 거리 (미터)", example = "500")
    val distance: Int,
    @Schema(description = "이동 시간 (분)", example = "10")
    val duration: Int,
    @Schema(description = "이동 수단 (WALK/SUBWAY/BUS/CAR)", example = "WALK")
    val transportType: String,
    @Schema(description = "이동 설명", example = "도보 10분 거리")
    val description: String,
)

@Schema(description = "코스 평가 응답")
data class RatingResponse(
    @Schema(description = "평가 ID", example = "rating-xyz789")
    val ratingId: String,
    @Schema(description = "코스 ID", example = "course-abc123")
    val courseId: String,
    @Schema(description = "평점 (1~5)", example = "4")
    val score: Int,
    @Schema(description = "평가 생성 시각")
    val createdAt: Instant,
)
