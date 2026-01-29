package com.dateclick.api.infrastructure.service

import com.dateclick.api.application.region.GetRegionsUseCase
import com.dateclick.api.domain.region.entity.Region
import com.dateclick.api.domain.region.port.RegionRepository
import com.dateclick.api.infrastructure.config.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class GetRegionsService(
    private val regionRepository: RegionRepository,
) : GetRegionsUseCase {
    @Cacheable(
        cacheNames = [CacheConfig.REGIONS_CACHE_NAME],
        key = "#city != null ? #city : 'all'",
    )
    override fun execute(city: String?): List<Region> {
        return if (city.isNullOrBlank()) {
            regionRepository.findAll()
        } else {
            regionRepository.findByCity(city)
        }
    }
}
