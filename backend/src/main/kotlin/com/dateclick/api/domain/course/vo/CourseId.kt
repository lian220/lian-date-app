package com.dateclick.api.domain.course.vo

@JvmInline
value class CourseId(val value: String) {
    init {
        require(value.startsWith("course_")) { "Invalid course ID format" }
    }

    companion object {
        fun generate(): CourseId = CourseId("course_${java.util.UUID.randomUUID()}")
    }
}
