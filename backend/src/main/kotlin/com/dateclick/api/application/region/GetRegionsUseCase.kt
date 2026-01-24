package com.dateclick.api.application.region

import com.dateclick.api.domain.region.entity.Region

interface GetRegionsUseCase {
    fun execute(city: String?): List<Region>
}
