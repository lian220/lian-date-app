package com.dateclick.api.domain.region.vo

@JvmInline
value class RegionId(val value: String) {
    init {
        require(value.isNotBlank()) { "Region ID cannot be blank" }
    }
}
