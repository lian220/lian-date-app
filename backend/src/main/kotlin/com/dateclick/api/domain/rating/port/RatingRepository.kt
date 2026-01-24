package com.dateclick.api.domain.rating.port

import com.dateclick.api.domain.course.vo.CourseId
import com.dateclick.api.domain.rating.entity.Rating

interface RatingRepository {
    suspend fun save(rating: Rating): Rating
    suspend fun findByCourseIdAndSessionId(courseId: CourseId, sessionId: String): Rating?
    suspend fun existsByCourseIdAndSessionId(courseId: CourseId, sessionId: String): Boolean
}
