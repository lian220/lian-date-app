package com.dateclick.api.presentation.rest.course

import com.dateclick.api.application.course.CreateCourseUseCase
import com.dateclick.api.application.course.GetCourseUseCase
import com.dateclick.api.application.course.RegenerateCourseUseCase
import com.dateclick.api.domain.course.vo.CourseId
import com.dateclick.api.presentation.mapper.CourseMapper
import com.dateclick.api.presentation.rest.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "Course", description = "데이트 코스 API")
@RestController
@RequestMapping("/v1/courses")
class CourseController(
    private val createCourseUseCase: CreateCourseUseCase?,
    private val getCourseUseCase: GetCourseUseCase,
    private val regenerateCourseUseCase: RegenerateCourseUseCase,
    private val courseMapper: CourseMapper
) {

    @Operation(summary = "코스 생성", description = "조건 기반 AI 데이트 코스 생성")
    @PostMapping
    fun createCourse(
        @RequestHeader("X-Session-Id") sessionId: String,
        @Valid @RequestBody request: CreateCourseRequest
    ): ApiResponse<CourseResponse> {
        // Use case implementation will be added in future ticket
        val command = courseMapper.toCommand(request, sessionId)

        if (createCourseUseCase != null) {
            val course = createCourseUseCase.execute(command)
            val response = courseMapper.toResponse(course)
            return ApiResponse.success(response)
        }

        // TODO: Remove when use case is implemented
        throw NotImplementedError("Use case implementation pending (LAD-XX)")
    }

    @Operation(summary = "코스 상세 조회", description = "코스 ID로 코스 정보 조회")
    @GetMapping("/{courseId}")
    fun getCourse(
        @PathVariable courseId: String
    ): ApiResponse<CourseResponse> {
        val course = getCourseUseCase.execute(CourseId(courseId))
            ?: throw IllegalArgumentException("Course not found: $courseId")

        val response = courseMapper.toResponse(course)
        return ApiResponse.success(response)
    }

    @Operation(summary = "코스 재생성", description = "기존 코스와 다른 새로운 코스 생성")
    @PostMapping("/{courseId}/regenerate")
    fun regenerateCourse(
        @RequestHeader("X-Session-Id") sessionId: String,
        @PathVariable courseId: String,
        @RequestBody request: RegenerateCourseRequest?
    ): ApiResponse<CourseResponse> {
        val command = courseMapper.toRegenerateCommand(courseId, request, sessionId)
        val course = regenerateCourseUseCase.execute(command)
        val response = courseMapper.toResponse(course)
        return ApiResponse.success(response)
    }

    @PostMapping("/{courseId}/ratings")
    fun rateCourse(
        @PathVariable courseId: String,
        @Valid @RequestBody request: RateCourseRequest
    ): ApiResponse<RatingResponse> {
        // TODO: Implement with RateCourseUseCase
        throw NotImplementedError("To be implemented")
    }
}
