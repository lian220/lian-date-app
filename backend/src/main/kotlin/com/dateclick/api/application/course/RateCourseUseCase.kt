package com.dateclick.api.application.course

import com.dateclick.api.domain.course.vo.CourseId
import com.dateclick.api.domain.rating.entity.Rating

interface RateCourseUseCase {
    fun execute(command: RateCourseCommand): Rating
}

data class RateCourseCommand(
    val courseId: CourseId,
    val sessionId: String,
    val score: Int,
)
