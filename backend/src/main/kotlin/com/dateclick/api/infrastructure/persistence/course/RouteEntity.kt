package com.dateclick.api.infrastructure.persistence.course

import jakarta.persistence.*

@Entity
@Table(name = "routes")
class RouteEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    val course: CourseEntity,
    @Column(name = "from_order", nullable = false)
    val fromOrder: Int,
    @Column(name = "to_order", nullable = false)
    val toOrder: Int,
    @Column(name = "distance", nullable = false)
    val distance: Int,
    @Column(name = "duration", nullable = false)
    val duration: Int,
    @Column(name = "transport_type", nullable = false, length = 20)
    val transportType: String,
    @Column(name = "description", nullable = false, length = 500)
    val description: String,
)
