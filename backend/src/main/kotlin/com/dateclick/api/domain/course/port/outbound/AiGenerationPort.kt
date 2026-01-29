package com.dateclick.api.domain.course.port.outbound

import com.dateclick.api.domain.course.vo.Budget
import com.dateclick.api.domain.course.vo.DateType
import com.dateclick.api.domain.place.vo.Location
import com.dateclick.api.domain.region.entity.Region

/**
 * AI 코스 생성 포트
 * MVP: AI가 장소 정보를 포함한 전체 코스를 생성
 */
interface AiGenerationPort {
    fun generateCourseRecommendation(
        region: Region,
        dateType: DateType,
        budget: Budget,
        specialRequest: String?,
    ): AiCourseRecommendation
}

data class AiCourseRecommendation(
    val places: List<AiRecommendedPlace>,
    val summary: String?,
)

data class AiRecommendedPlace(
    val order: Int,
    val name: String,
    val category: String,
    val categoryDetail: String?,
    val address: String,
    val roadAddress: String?,
    val location: Location,
    val phone: String?,
    val recommendReason: String,
    val estimatedCost: Int,
    val estimatedDuration: Int,
    val recommendedTime: String?,
)
