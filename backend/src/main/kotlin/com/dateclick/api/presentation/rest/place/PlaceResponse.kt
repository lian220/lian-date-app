package com.dateclick.api.presentation.rest.place

import com.dateclick.api.domain.place.entity.Place
import com.dateclick.api.domain.place.vo.BusinessHours
import com.dateclick.api.domain.place.vo.PlaceCurationInfo
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
    val recommendation: String,
) {
    companion object {
        /**
         * Domain PlaceCurationInfo를 Response DTO로 변환
         */
        fun from(info: PlaceCurationInfo) =
            PlaceCurationResponse(
                dateScore = info.dateScore,
                moodTags = info.moodTags,
                priceRange = info.priceRange,
                bestTime = info.bestTime,
                recommendation = info.recommendation,
            )
    }
}

/**
 * 장소 상세 정보 응답 DTO
 */
@Schema(description = "장소 상세 정보")
data class PlaceDetailResponse(
    @Schema(description = "장소 ID", example = "12345")
    val id: String,
    @Schema(description = "장소명", example = "카페 온더문")
    val name: String,
    @Schema(description = "카테고리", example = "카페")
    val category: String,
    @Schema(description = "상세 카테고리", example = "카페 > 디저트카페")
    val categoryDetail: String?,
    @Schema(description = "위도", example = "37.5665")
    val latitude: Double,
    @Schema(description = "경도", example = "126.9780")
    val longitude: Double,
    @Schema(description = "주소", example = "서울 마포구 연남동 123-45")
    val address: String,
    @Schema(description = "도로명 주소", example = "서울 마포구 연남로 12길 34")
    val roadAddress: String?,
    @Schema(description = "전화번호", example = "02-1234-5678")
    val phone: String?,
    @Schema(description = "영업시간 정보")
    val businessHours: List<BusinessHoursResponse>?,
    @Schema(description = "장소 이미지 URL 목록")
    val imageUrls: List<String>?,
    @Schema(description = "카카오맵 URL", example = "https://place.map.kakao.com/12345")
    val kakaoPlaceUrl: String?,
    @Schema(description = "카카오맵 평점", example = "4.5")
    val kakaoRating: Double?,
    @Schema(description = "카카오맵 리뷰 수", example = "123")
    val kakaoReviewCount: Int?,
) {
    companion object {
        /**
         * Domain Place를 Response DTO로 변환
         */
        fun from(place: Place) =
            PlaceDetailResponse(
                id = place.id.value,
                name = place.name,
                category = place.category,
                categoryDetail = place.categoryDetail,
                latitude = place.location.lat,
                longitude = place.location.lng,
                address = place.address,
                roadAddress = place.roadAddress,
                phone = place.phone,
                businessHours = place.businessHours?.map { BusinessHoursResponse.from(it) },
                imageUrls = place.imageUrls,
                kakaoPlaceUrl = place.kakaoPlaceUrl,
                kakaoRating = place.kakaoRating,
                kakaoReviewCount = place.kakaoReviewCount,
            )
    }
}

/**
 * 영업시간 응답 DTO
 */
@Schema(description = "영업시간 정보")
data class BusinessHoursResponse(
    @Schema(description = "요일", example = "월요일")
    val day: String,
    @Schema(description = "오픈 시간", example = "10:00")
    val open: String?,
    @Schema(description = "마감 시간", example = "22:00")
    val close: String?,
) {
    companion object {
        fun from(businessHours: BusinessHours) =
            BusinessHoursResponse(
                day = businessHours.day,
                open = businessHours.open,
                close = businessHours.close,
            )
    }
}
