package com.dateclick.api.infrastructure.persistence.course

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CourseJpaRepository : JpaRepository<CourseEntity, String> {
    fun findBySessionId(sessionId: String): List<CourseEntity>
}
