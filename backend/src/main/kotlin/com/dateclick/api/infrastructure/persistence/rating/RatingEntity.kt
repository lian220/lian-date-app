package com.dateclick.api.infrastructure.persistence.rating

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(
    name = "ratings",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_ratings_course_session",
            columnNames = ["course_id", "session_id"],
        ),
    ],
    indexes = [
        Index(name = "idx_ratings_course_id", columnList = "course_id"),
        Index(name = "idx_ratings_session_id", columnList = "session_id"),
    ],
)
class RatingEntity(
    @Id
    @Column(name = "id", nullable = false, length = 50)
    val id: String,
    @Column(name = "course_id", nullable = false, length = 50)
    val courseId: String,
    @Column(name = "session_id", nullable = false, length = 100)
    val sessionId: String,
    @Column(name = "score", nullable = false)
    val score: Int,
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
