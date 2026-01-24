package com.dateclick.api.application.course

import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.domain.course.vo.Budget
import com.dateclick.api.domain.course.vo.DateType
import com.dateclick.api.domain.region.vo.RegionId

interface CreateCourseUseCase {
    suspend fun execute(command: CreateCourseCommand): Course
}

data class CreateCourseCommand(
    val regionId: RegionId,
    val dateType: DateType,
    val budget: Budget,
    val specialRequest: String?,
    val sessionId: String
)
