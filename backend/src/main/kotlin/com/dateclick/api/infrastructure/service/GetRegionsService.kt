package com.dateclick.api.infrastructure.service

import com.dateclick.api.application.region.GetRegionsUseCase
import com.dateclick.api.domain.region.entity.Region
import com.dateclick.api.domain.region.port.RegionRepository
import org.springframework.stereotype.Service

@Service
class GetRegionsService(
    private val regionRepository: RegionRepository
) : GetRegionsUseCase {

    override fun execute(city: String?): List<Region> {
        return if (city.isNullOrBlank()) {
            regionRepository.findAll()
        } else {
            regionRepository.findByCity(city)
        }
    }
}
