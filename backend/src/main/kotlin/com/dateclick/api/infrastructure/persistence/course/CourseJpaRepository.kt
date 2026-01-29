package com.dateclick.api.infrastructure.persistence.course

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CourseJpaRepository : JpaRepository<CourseEntity, String> {
    fun findBySessionId(sessionId: String): List<CourseEntity>

    @Query(
        """
        SELECT DISTINCT c FROM CourseEntity c
        LEFT JOIN FETCH c.places
        WHERE c.id = :id
    """,
    )
    fun findByIdWithPlaces(
        @Param("id") id: String,
    ): Optional<CourseEntity>

    @Query(
        """
        SELECT DISTINCT c FROM CourseEntity c
        LEFT JOIN FETCH c.routes
        WHERE c.id = :id
    """,
    )
    fun findByIdWithRoutes(
        @Param("id") id: String,
    ): Optional<CourseEntity>
}
