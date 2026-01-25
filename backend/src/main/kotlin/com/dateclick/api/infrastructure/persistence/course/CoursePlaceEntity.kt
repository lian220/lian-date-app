package com.dateclick.api.infrastructure.persistence.course

import jakarta.persistence.*

@Entity
@Table(name = "course_places")
class CoursePlaceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    val course: CourseEntity,

    @Column(name = "place_order", nullable = false)
    val order: Int,

    @Column(name = "place_id", nullable = false, length = 50)
    val placeId: String,

    @Column(name = "name", nullable = false, length = 200)
    val name: String,

    @Column(name = "category", nullable = false, length = 50)
    val category: String,

    @Column(name = "category_detail", length = 100)
    val categoryDetail: String? = null,

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    val address: String,

    @Column(name = "road_address", columnDefinition = "TEXT")
    val roadAddress: String? = null,

    @Column(name = "lat", nullable = false)
    val lat: Double,

    @Column(name = "lng", nullable = false)
    val lng: Double,

    @Column(name = "phone", length = 20)
    val phone: String? = null,

    @Column(name = "estimated_cost", nullable = false)
    val estimatedCost: Int,

    @Column(name = "estimated_duration", nullable = false)
    val estimatedDuration: Int,

    @Column(name = "recommended_time", length = 20)
    val recommendedTime: String? = null,

    @Column(name = "recommend_reason", nullable = false, columnDefinition = "TEXT")
    val recommendReason: String,

    @Column(name = "image_url", columnDefinition = "TEXT")
    val imageUrl: String? = null,

    @Column(name = "kakao_place_url", columnDefinition = "TEXT")
    val kakaoPlaceUrl: String? = null
)
