package com.dateclick.api.domain.rating.entity

import com.dateclick.api.domain.course.vo.CourseId
import java.time.Instant
import java.util.UUID

data class Rating(
    val id: String,
    val courseId: CourseId,
    val sessionId: String,
    val score: Int,
    val createdAt: Instant
) {
    init {
        require(score in 1..5) { "Score must be between 1 and 5" }
    }

    companion object {
        fun create(courseId: CourseId, sessionId: String, score: Int): Rating =
            Rating(
                id = "rating_${UUID.randomUUID()}",
                courseId = courseId,
                sessionId = sessionId,
                score = score,
                createdAt = Instant.now()
            )
    }
}
