package com.dateclick.api.domain.course.port.outbound

import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.domain.course.vo.CourseId

interface CourseRepository {
    fun save(course: Course): Course
    fun findById(id: CourseId): Course?
    fun findBySessionId(sessionId: String): List<Course>
}
