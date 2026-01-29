package com.dateclick.api.infrastructure.persistence.region

import jakarta.persistence.*

@Entity
@Table(name = "regions")
class RegionEntity(
    @Id
    @Column(name = "id", nullable = false, length = 50)
    val id: String,
    @Column(name = "name", nullable = false, length = 100)
    val name: String,
    @Column(name = "city", nullable = false, length = 50)
    val city: String,
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    val description: String,
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "region_keywords", joinColumns = [JoinColumn(name = "region_id")])
    @Column(name = "keyword", nullable = false)
    val keywords: List<String>,
    @Column(name = "center_lat", nullable = false)
    val centerLat: Double,
    @Column(name = "center_lng", nullable = false)
    val centerLng: Double,
)
