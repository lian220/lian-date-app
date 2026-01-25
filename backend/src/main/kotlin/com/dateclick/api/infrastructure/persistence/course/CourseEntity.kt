package com.dateclick.api.infrastructure.persistence.course

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(name = "courses")
class CourseEntity(
    @Id
    @Column(name = "id", nullable = false, length = 50)
    val id: String,

    @Column(name = "region_id", nullable = false, length = 50)
    val regionId: String,

    @Column(name = "region_name", nullable = false, length = 100)
    val regionName: String,

    @Column(name = "date_type", nullable = false, length = 20)
    val dateType: String,

    @Column(name = "budget_min", nullable = false)
    val budgetMin: Int,

    @Column(name = "budget_max", nullable = false)
    val budgetMax: Int,

    @OneToMany(
        mappedBy = "course",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @OrderBy("order ASC")
    val places: List<CoursePlaceEntity> = emptyList(),

    @OneToMany(
        mappedBy = "course",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @OrderBy("fromOrder ASC")
    val routes: List<RouteEntity> = emptyList(),

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "session_id", length = 100)
    val sessionId: String? = null
)
