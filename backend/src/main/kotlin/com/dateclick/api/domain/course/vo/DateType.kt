package com.dateclick.api.domain.course.vo

enum class DateType(val code: String, val displayName: String) {
    ROMANTIC("romantic", "감성/로맨틱"),
    ACTIVITY("activity", "액티비티"),
    FOOD("food", "맛집 탐방"),
    CULTURE("culture", "문화/예술"),
    HEALING("healing", "힐링");

    companion object {
        fun fromCode(code: String): DateType =
            entries.find { it.code == code }
                ?: throw IllegalArgumentException("Invalid date type code: $code")
    }
}
