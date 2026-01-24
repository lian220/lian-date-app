package com.dateclick.api.domain.course.port.outbound

import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.domain.course.vo.CourseId

interface CourseRepository {
    suspend fun save(course: Course): Course
    suspend fun findById(id: CourseId): Course?
    suspend fun findBySessionId(sessionId: String): List<Course>
}
