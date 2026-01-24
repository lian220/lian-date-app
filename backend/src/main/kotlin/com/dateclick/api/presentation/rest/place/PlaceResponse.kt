package com.dateclick.api.presentation.rest.place

data class PlaceDetailResponse(
    val placeId: String,
    val name: String,
    val category: String,
    val categoryDetail: String?,
    val address: String,
    val roadAddress: String?,
    val lat: Double,
    val lng: Double,
    val phone: String?,
    val businessHours: List<BusinessHoursResponse>?,
    val imageUrls: List<String>?,
    val kakaoPlaceUrl: String?,
    val kakaoRating: Double?,
    val kakaoReviewCount: Int?
)

data class BusinessHoursResponse(
    val day: String,
    val open: String?,
    val close: String?
)
