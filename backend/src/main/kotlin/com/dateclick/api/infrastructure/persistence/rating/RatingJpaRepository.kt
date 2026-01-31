package com.dateclick.api.infrastructure.persistence.rating

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RatingJpaRepository : JpaRepository<RatingEntity, String> {
    fun findByCourseIdAndSessionId(
        courseId: String,
        sessionId: String,
    ): RatingEntity?

    fun existsByCourseIdAndSessionId(
        courseId: String,
        sessionId: String,
    ): Boolean
}
