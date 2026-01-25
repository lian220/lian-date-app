package com.dateclick.api.presentation.rest.course

import com.dateclick.api.application.course.CreateCourseUseCase
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

    @GetMapping("/{courseId}")
    fun getCourse(
        @PathVariable courseId: String
    ): ApiResponse<CourseResponse> {
        // TODO: Implement with GetCourseUseCase
        throw NotImplementedError("To be implemented")
    }

    @PostMapping("/{courseId}/regenerate")
    fun regenerateCourse(
        @RequestHeader("X-Session-Id") sessionId: String,
        @PathVariable courseId: String,
        @RequestBody request: RegenerateCourseRequest?
    ): ApiResponse<CourseResponse> {
        // TODO: Implement with RegenerateCourseUseCase
        throw NotImplementedError("To be implemented")
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
