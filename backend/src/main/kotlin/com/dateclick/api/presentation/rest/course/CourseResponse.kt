package com.dateclick.api.presentation.rest.course

import java.time.Instant

data class CourseResponse(
    val courseId: String,
    val regionId: String,
    val regionName: String,
    val dateType: String,
    val budget: String,
    val totalEstimatedCost: Int,
    val places: List<CoursePlaceResponse>,
    val routes: List<RouteResponse>,
    val createdAt: Instant
)

data class CoursePlaceResponse(
    val order: Int,
    val placeId: String,
    val name: String,
    val category: String,
    val categoryDetail: String?,
    val address: String,
    val roadAddress: String?,
    val lat: Double,
    val lng: Double,
    val phone: String?,
    val estimatedCost: Int,
    val estimatedDuration: Int,
    val recommendedTime: String?,
    val recommendReason: String,
    val imageUrl: String?,
    val kakaoPlaceUrl: String
)

data class RouteResponse(
    val from: Int,
    val to: Int,
    val distance: Int,
    val duration: Int,
    val transportType: String,
    val description: String
)

data class RatingResponse(
    val ratingId: String,
    val courseId: String,
    val score: Int,
    val createdAt: Instant
)
