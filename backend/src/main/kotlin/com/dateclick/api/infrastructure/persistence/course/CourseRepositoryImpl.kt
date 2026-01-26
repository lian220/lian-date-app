package com.dateclick.api.infrastructure.persistence.course

import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.domain.course.port.outbound.CourseRepository
import com.dateclick.api.domain.course.vo.CourseId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class CourseRepositoryImpl(
    private val jpaRepository: CourseJpaRepository
) : CourseRepository {

    @Transactional
    override fun save(course: Course): Course {
        val entity = CourseMapper.toEntity(course)
        val saved = jpaRepository.save(entity)
        return CourseMapper.toDomain(saved)
    }

    override fun findById(id: CourseId): Course? {
        // 두 번의 쿼리로 places와 routes를 모두 eager fetch
        val courseWithPlaces = jpaRepository.findByIdWithPlaces(id.value).orElse(null) ?: return null
        // routes도 fetch
        jpaRepository.findByIdWithRoutes(id.value)

        return CourseMapper.toDomain(courseWithPlaces)
    }

    override fun findBySessionId(sessionId: String): List<Course> {
        return jpaRepository.findBySessionId(sessionId)
            .map { CourseMapper.toDomain(it) }
    }
}
