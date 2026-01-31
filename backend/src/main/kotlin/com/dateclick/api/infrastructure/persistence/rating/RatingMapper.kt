package com.dateclick.api.infrastructure.persistence.rating

import com.dateclick.api.domain.course.vo.CourseId
import com.dateclick.api.domain.rating.entity.Rating

object RatingMapper {
    fun toDomain(entity: RatingEntity): Rating =
        Rating(
            id = entity.id,
            courseId = CourseId(entity.courseId),
            sessionId = entity.sessionId,
            score = entity.score,
            createdAt = entity.createdAt,
        )

    fun toEntity(domain: Rating): RatingEntity =
        RatingEntity(
            id = domain.id,
            courseId = domain.courseId.value,
            sessionId = domain.sessionId,
            score = domain.score,
            createdAt = domain.createdAt,
        )
}
