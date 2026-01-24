package com.dateclick.api.domain.course.entity

import com.dateclick.api.domain.course.vo.Budget
import com.dateclick.api.domain.course.vo.CourseId
import com.dateclick.api.domain.course.vo.DateType
import com.dateclick.api.domain.course.vo.EstimatedCost
import com.dateclick.api.domain.region.vo.RegionId
import java.time.Instant

data class Course(
    val id: CourseId,
    val regionId: RegionId,
    val regionName: String,
    val dateType: DateType,
    val budget: Budget,
    val places: List<CoursePlace>,
    val routes: List<Route>,
    val createdAt: Instant
) {
    val totalEstimatedCost: EstimatedCost
        get() = places
            .map { it.estimatedCost }
            .fold(EstimatedCost.ZERO) { acc, cost -> acc + cost }

    fun regenerate(newPlaces: List<CoursePlace>, newRoutes: List<Route>): Course =
        Course(
            id = CourseId.generate(),
            regionId = this.regionId,
            regionName = this.regionName,
            dateType = this.dateType,
            budget = this.budget,
            places = newPlaces,
            routes = newRoutes,
            createdAt = Instant.now()
        )

    companion object {
        fun create(
            regionId: RegionId,
            regionName: String,
            dateType: DateType,
            budget: Budget,
            places: List<CoursePlace>,
            routes: List<Route>
        ): Course = Course(
            id = CourseId.generate(),
            regionId = regionId,
            regionName = regionName,
            dateType = dateType,
            budget = budget,
            places = places,
            routes = routes,
            createdAt = Instant.now()
        )
    }
}
