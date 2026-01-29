package com.dateclick.api.application.course

import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.domain.course.vo.CourseId

interface RegenerateCourseUseCase {
    fun execute(command: RegenerateCourseCommand): Course
}

data class RegenerateCourseCommand(
    val originalCourseId: CourseId,
    val excludePlaceIds: List<String>,
    val sessionId: String,
)
