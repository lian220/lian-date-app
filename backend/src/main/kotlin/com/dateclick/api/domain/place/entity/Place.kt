package com.dateclick.api.domain.place.entity

import com.dateclick.api.domain.place.vo.BusinessHours
import com.dateclick.api.domain.place.vo.Location
import com.dateclick.api.domain.place.vo.PlaceId

data class Place(
    val id: PlaceId,
    val name: String,
    val category: String,
    val categoryDetail: String?,
    val location: Location,
    val address: String,
    val roadAddress: String?,
    val phone: String?,
    val businessHours: List<BusinessHours>? = null,
    val imageUrls: List<String>? = null,
    val kakaoPlaceUrl: String? = null,
    val kakaoRating: Double? = null,
    val kakaoReviewCount: Int? = null
)
