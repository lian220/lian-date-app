package com.dateclick.api.domain.course.vo

@JvmInline
value class EstimatedCost(val value: Int) {
    init {
        require(value >= 0) { "Estimated cost cannot be negative" }
    }

    operator fun plus(other: EstimatedCost): EstimatedCost = EstimatedCost(this.value + other.value)

    companion object {
        val ZERO = EstimatedCost(0)
    }
}
