package com.dateclick.api.domain.region.port

import com.dateclick.api.domain.region.entity.Region
import com.dateclick.api.domain.region.vo.RegionId

interface RegionRepository {
    suspend fun findAll(): List<Region>
    suspend fun findById(id: RegionId): Region?
    suspend fun findByCity(city: String): List<Region>
}
