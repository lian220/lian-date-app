package com.dateclick.api.infrastructure.persistence.region

import com.dateclick.api.domain.region.entity.Region
import com.dateclick.api.domain.region.vo.RegionId

object RegionMapper {
    fun toDomain(entity: RegionEntity): Region {
        return Region(
            id = RegionId(entity.id),
            name = entity.name,
            city = entity.city,
            description = entity.description,
            keywords = entity.keywords,
            centerLat = entity.centerLat,
            centerLng = entity.centerLng,
        )
    }

    fun toEntity(domain: Region): RegionEntity {
        return RegionEntity(
            id = domain.id.value,
            name = domain.name,
            city = domain.city,
            description = domain.description,
            keywords = domain.keywords,
            centerLat = domain.centerLat,
            centerLng = domain.centerLng,
        )
    }
}
