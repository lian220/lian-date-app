package com.dateclick.api.infrastructure.persistence.rating

import com.dateclick.api.domain.course.vo.CourseId
import com.dateclick.api.domain.rating.entity.Rating
import com.dateclick.api.domain.rating.port.RatingRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class RatingRepositoryImpl(
    private val jpaRepository: RatingJpaRepository,
) : RatingRepository {
    @Transactional
    override fun save(rating: Rating): Rating {
        val entity = RatingMapper.toEntity(rating)
        val saved = jpaRepository.save(entity)
        return RatingMapper.toDomain(saved)
    }

    override fun findByCourseIdAndSessionId(
        courseId: CourseId,
        sessionId: String,
    ): Rating? =
        jpaRepository
            .findByCourseIdAndSessionId(courseId.value, sessionId)
            ?.let { RatingMapper.toDomain(it) }

    override fun existsByCourseIdAndSessionId(
        courseId: CourseId,
        sessionId: String,
    ): Boolean = jpaRepository.existsByCourseIdAndSessionId(courseId.value, sessionId)
}
