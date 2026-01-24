package com.dateclick.api.presentation.rest.course

import com.dateclick.api.presentation.rest.common.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/courses")
class CourseController {

    @PostMapping
    fun createCourse(
        @RequestHeader("X-Session-Id") sessionId: String,
        @Valid @RequestBody request: CreateCourseRequest
    ): ApiResponse<CourseResponse> {
        // TODO: Implement with CreateCourseUseCase
        throw NotImplementedError("To be implemented")
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
