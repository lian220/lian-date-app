package com.dateclick.api.domain.rating.port

import com.dateclick.api.domain.course.vo.CourseId
import com.dateclick.api.domain.rating.entity.Rating

interface RatingRepository {
    fun save(rating: Rating): Rating

    fun findByCourseIdAndSessionId(
        courseId: CourseId,
        sessionId: String,
    ): Rating?

    fun existsByCourseIdAndSessionId(
        courseId: CourseId,
        sessionId: String,
    ): Boolean
}
