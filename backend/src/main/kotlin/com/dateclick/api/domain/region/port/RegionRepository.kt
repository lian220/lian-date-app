package com.dateclick.api.domain.region.port

import com.dateclick.api.domain.region.entity.Region
import com.dateclick.api.domain.region.vo.RegionId

interface RegionRepository {
    fun findAll(): List<Region>

    fun findById(id: RegionId): Region?

    fun findByCity(city: String): List<Region>
}
