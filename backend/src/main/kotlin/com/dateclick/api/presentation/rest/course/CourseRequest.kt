package com.dateclick.api.presentation.rest.course

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "데이트 코스 생성 요청")
data class CreateCourseRequest(
    @Schema(description = "지역 ID", example = "region-hongdae")
    @field:NotBlank(message = "지역을 선택해주세요")
    val regionId: String,
    @Schema(description = "데이트 유형 (ROMANTIC/ACTIVITY/FOOD/CULTURE)", example = "ROMANTIC")
    @field:NotBlank(message = "데이트 유형을 선택해주세요")
    val dateType: String,
    @Schema(description = "예산 (LOW/MEDIUM/HIGH)", example = "MEDIUM")
    @field:NotBlank(message = "예산을 선택해주세요")
    val budget: String,
    @Schema(description = "특별 요청 사항 (최대 100자)", example = "반려동물 동반 가능한 곳 포함해주세요", nullable = true)
    @field:Size(max = 100, message = "특별 요청은 100자 이하로 입력해주세요")
    val specialRequest: String? = null,
)

@Schema(description = "코스 재생성 요청")
data class RegenerateCourseRequest(
    @Schema(description = "제외할 장소 ID 목록", nullable = true)
    val excludePlaceIds: List<String>? = null,
)

@Schema(description = "코스 만족도 평가 요청")
data class RateCourseRequest(
    @Schema(description = "평점 (1~5점)", example = "4", minimum = "1", maximum = "5")
    @field:Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @field:Max(value = 5, message = "평점은 5점 이하여야 합니다")
    val score: Int,
    @Schema(description = "세션 ID", example = "sess-abc123")
    @field:NotBlank(message = "세션 ID가 필요합니다")
    val sessionId: String,
)
