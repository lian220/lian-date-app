package com.dateclick.api.infrastructure.persistence.course

import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.domain.course.port.outbound.CourseRepository
import com.dateclick.api.domain.course.vo.CourseId
import org.springframework.stereotype.Component

@Component
class CourseRepositoryImpl(
    private val jpaRepository: CourseJpaRepository
) : CourseRepository {

    override fun save(course: Course): Course {
        val entity = CourseMapper.toEntity(course)
        val saved = jpaRepository.save(entity)
        return CourseMapper.toDomain(saved)
    }

    override fun findById(id: CourseId): Course? {
        return jpaRepository.findById(id.value)
            .map { CourseMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findBySessionId(sessionId: String): List<Course> {
        return jpaRepository.findBySessionId(sessionId)
            .map { CourseMapper.toDomain(it) }
    }
}
