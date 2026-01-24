package com.dateclick.api.domain.course.port.outbound

import com.dateclick.api.domain.course.vo.Budget
import com.dateclick.api.domain.course.vo.DateType
import com.dateclick.api.domain.place.entity.Place
import com.dateclick.api.domain.region.entity.Region

interface AiGenerationPort {
    suspend fun generateCourseRecommendation(
        region: Region,
        dateType: DateType,
        budget: Budget,
        specialRequest: String?,
        candidatePlaces: List<Place>
    ): AiCourseRecommendation
}

data class AiCourseRecommendation(
    val places: List<AiRecommendedPlace>,
    val summary: String?
)

data class AiRecommendedPlace(
    val placeId: String,
    val order: Int,
    val recommendReason: String,
    val estimatedCost: Int,
    val estimatedDuration: Int,
    val recommendedTime: String?
)
