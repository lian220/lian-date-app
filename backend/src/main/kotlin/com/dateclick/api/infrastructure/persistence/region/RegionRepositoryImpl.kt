package com.dateclick.api.infrastructure.persistence.region

import com.dateclick.api.domain.region.entity.Region
import com.dateclick.api.domain.region.port.RegionRepository
import com.dateclick.api.domain.region.vo.RegionId
import org.springframework.stereotype.Component

@Component
class RegionRepositoryImpl(
    private val jpaRepository: RegionJpaRepository,
) : RegionRepository {
    override fun findAll(): List<Region> {
        return jpaRepository.findAll()
            .map { RegionMapper.toDomain(it) }
    }

    override fun findById(id: RegionId): Region? {
        return jpaRepository.findById(id.value)
            .map { RegionMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByCity(city: String): List<Region> {
        return jpaRepository.findByCity(city)
            .map { RegionMapper.toDomain(it) }
    }
}
