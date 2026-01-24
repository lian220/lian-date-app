package com.dateclick.api.presentation.rest.course

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateCourseRequest(
    @field:NotBlank(message = "지역을 선택해주세요")
    val regionId: String,

    @field:NotBlank(message = "데이트 유형을 선택해주세요")
    val dateType: String,

    @field:NotBlank(message = "예산을 선택해주세요")
    val budget: String,

    @field:Size(max = 100, message = "특별 요청은 100자 이하로 입력해주세요")
    val specialRequest: String? = null
)

data class RegenerateCourseRequest(
    val excludePlaceIds: List<String>? = null
)

data class RateCourseRequest(
    @field:Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @field:Max(value = 5, message = "평점은 5점 이하여야 합니다")
    val score: Int,

    @field:NotBlank(message = "세션 ID가 필요합니다")
    val sessionId: String
)
