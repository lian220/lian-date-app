package com.dateclick.api.infrastructure.persistence.region

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RegionJpaRepository : JpaRepository<RegionEntity, String> {
    fun findByCity(city: String): List<RegionEntity>
}
