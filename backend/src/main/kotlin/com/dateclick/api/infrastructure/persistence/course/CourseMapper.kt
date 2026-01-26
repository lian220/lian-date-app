package com.dateclick.api.infrastructure.persistence.course

import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.domain.course.entity.CoursePlace
import com.dateclick.api.domain.course.entity.Route
import com.dateclick.api.domain.course.entity.TransportType
import com.dateclick.api.domain.course.vo.Budget
import com.dateclick.api.domain.course.vo.CourseId
import com.dateclick.api.domain.course.vo.DateType
import com.dateclick.api.domain.course.vo.EstimatedCost
import com.dateclick.api.domain.place.vo.Location
import com.dateclick.api.domain.place.vo.PlaceId
import com.dateclick.api.domain.region.vo.RegionId

object CourseMapper {
    fun toDomain(entity: CourseEntity): Course {
        return Course(
            id = CourseId(entity.id),
            regionId = RegionId(entity.regionId),
            regionName = entity.regionName,
            dateType = DateType.fromCode(entity.dateType),
            budget = Budget(entity.budgetMin..entity.budgetMax),
            places = entity.places.map { toCoursePlaceDomain(it) },
            routes = entity.routes.map { toRouteDomain(it) },
            createdAt = entity.createdAt,
            sessionId = entity.sessionId,
        )
    }

    fun toEntity(domain: Course): CourseEntity {
        val courseEntity =
            CourseEntity(
                id = domain.id.value,
                regionId = domain.regionId.value,
                regionName = domain.regionName,
                dateType = domain.dateType.code,
                budgetMin = domain.budget.value.first,
                budgetMax = domain.budget.value.last,
                createdAt = domain.createdAt,
                sessionId = domain.sessionId,
            )

        // Set bidirectional relationships
        val placeEntities = domain.places.map { toCoursePlaceEntity(it, courseEntity) }
        val routeEntities = domain.routes.map { toRouteEntity(it, courseEntity) }

        // Use reflection to set the collections (since they are immutable in constructor)
        setCourseRelations(courseEntity, placeEntities, routeEntities)

        return courseEntity
    }

    private fun toCoursePlaceDomain(entity: CoursePlaceEntity): CoursePlace {
        return CoursePlace(
            order = entity.order,
            placeId = PlaceId(entity.placeId),
            name = entity.name,
            category = entity.category,
            categoryDetail = entity.categoryDetail,
            address = entity.address,
            roadAddress = entity.roadAddress,
            location = Location(entity.lat, entity.lng),
            phone = entity.phone,
            estimatedCost = EstimatedCost(entity.estimatedCost),
            estimatedDuration = entity.estimatedDuration,
            recommendedTime = entity.recommendedTime,
            recommendReason = entity.recommendReason,
            imageUrl = entity.imageUrl,
            kakaoPlaceUrl = entity.kakaoPlaceUrl,
        )
    }

    private fun toCoursePlaceEntity(
        domain: CoursePlace,
        course: CourseEntity,
    ): CoursePlaceEntity {
        return CoursePlaceEntity(
            course = course,
            order = domain.order,
            placeId = domain.placeId.value,
            name = domain.name,
            category = domain.category,
            categoryDetail = domain.categoryDetail,
            address = domain.address,
            roadAddress = domain.roadAddress,
            lat = domain.location.lat,
            lng = domain.location.lng,
            phone = domain.phone,
            estimatedCost = domain.estimatedCost.value,
            estimatedDuration = domain.estimatedDuration,
            recommendedTime = domain.recommendedTime,
            recommendReason = domain.recommendReason,
            imageUrl = domain.imageUrl,
            kakaoPlaceUrl = domain.kakaoPlaceUrl,
        )
    }

    private fun toRouteDomain(entity: RouteEntity): Route {
        return Route(
            from = entity.fromOrder,
            to = entity.toOrder,
            distance = entity.distance,
            duration = entity.duration,
            transportType = TransportType.fromCode(entity.transportType),
            description = entity.description,
        )
    }

    private fun toRouteEntity(
        domain: Route,
        course: CourseEntity,
    ): RouteEntity {
        return RouteEntity(
            course = course,
            fromOrder = domain.from,
            toOrder = domain.to,
            distance = domain.distance,
            duration = domain.duration,
            transportType = domain.transportType.code,
            description = domain.description,
        )
    }

    private fun setCourseRelations(
        course: CourseEntity,
        places: List<CoursePlaceEntity>,
        routes: List<RouteEntity>,
    ) {
        val placesField = CourseEntity::class.java.getDeclaredField("places")
        placesField.isAccessible = true
        placesField.set(course, places)

        val routesField = CourseEntity::class.java.getDeclaredField("routes")
        routesField.isAccessible = true
        routesField.set(course, routes)
    }
}
