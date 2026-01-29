package com.dateclick.api.domain.course.vo

@JvmInline
value class Budget(val value: IntRange) {
    companion object {
        fun from(budgetString: String): Budget =
            when (budgetString) {
                "0-30000" -> Budget(0..30000)
                "30000-50000" -> Budget(30000..50000)
                "50000-100000" -> Budget(50000..100000)
                "100000-" -> Budget(100000..Int.MAX_VALUE)
                else -> throw IllegalArgumentException("Invalid budget: $budgetString")
            }
    }

    fun toDisplayName(): String =
        when {
            value.first == 0 && value.last == 30000 -> "~3만원"
            value.first == 30000 && value.last == 50000 -> "3~5만원"
            value.first == 50000 && value.last == 100000 -> "5~10만원"
            value.first == 100000 && value.last == Int.MAX_VALUE -> "10만원~"
            else -> "${value.first}~${value.last}원"
        }
}
