package com.dateclick.api.domain.course.entity

import com.dateclick.api.domain.course.vo.EstimatedCost
import com.dateclick.api.domain.place.vo.Location
import com.dateclick.api.domain.place.vo.PlaceId

data class CoursePlace(
    val order: Int,
    val placeId: PlaceId,
    val name: String,
    val category: String,
    val categoryDetail: String?,
    val address: String,
    val roadAddress: String?,
    val location: Location,
    val phone: String?,
    val estimatedCost: EstimatedCost,
    val estimatedDuration: Int, // minutes
    val recommendedTime: String?, // HH:mm format
    val recommendReason: String,
    val imageUrl: String?,
    val kakaoPlaceUrl: String?
)
