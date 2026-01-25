package com.dateclick.api.presentation.mapper

import com.dateclick.api.application.course.CreateCourseCommand
import com.dateclick.api.application.course.RegenerateCourseCommand
import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.domain.course.vo.Budget
import com.dateclick.api.domain.course.vo.CourseId
import com.dateclick.api.domain.course.vo.DateType
import com.dateclick.api.domain.region.vo.RegionId
import com.dateclick.api.presentation.rest.course.CoursePlaceResponse
import com.dateclick.api.presentation.rest.course.CourseResponse
import com.dateclick.api.presentation.rest.course.CreateCourseRequest
import com.dateclick.api.presentation.rest.course.RegenerateCourseRequest
import com.dateclick.api.presentation.rest.course.RouteResponse
import org.springframework.stereotype.Component

@Component
class CourseMapper {

    fun toCommand(request: CreateCourseRequest, sessionId: String): CreateCourseCommand {
        return CreateCourseCommand(
            regionId = RegionId(request.regionId),
            dateType = DateType.fromCode(request.dateType),
            budget = Budget.from(request.budget),
            specialRequest = request.specialRequest,
            sessionId = sessionId
        )
    }

    fun toRegenerateCommand(
        courseId: String,
        request: RegenerateCourseRequest?,
        sessionId: String
    ): RegenerateCourseCommand {
        return RegenerateCourseCommand(
            originalCourseId = CourseId(courseId),
            excludePlaceIds = request?.excludePlaceIds ?: emptyList(),
            sessionId = sessionId
        )
    }

    fun toResponse(course: Course): CourseResponse {
        return CourseResponse(
            courseId = course.id.value,
            regionId = course.regionId.value,
            regionName = course.regionName,
            dateType = course.dateType.code,
            budget = course.budget.toDisplayName(),
            totalEstimatedCost = course.totalEstimatedCost.value,
            places = course.places.map { toCoursePlaceResponse(it) },
            routes = course.routes.map { toRouteResponse(it) },
            createdAt = course.createdAt
        )
    }

    private fun toCoursePlaceResponse(place: com.dateclick.api.domain.course.entity.CoursePlace): CoursePlaceResponse {
        return CoursePlaceResponse(
            order = place.order,
            placeId = place.placeId.value,
            name = place.name,
            category = place.category,
            categoryDetail = place.categoryDetail,
            address = place.address,
            roadAddress = place.roadAddress,
            lat = place.location.lat,
            lng = place.location.lng,
            phone = place.phone,
            estimatedCost = place.estimatedCost.value,
            estimatedDuration = place.estimatedDuration,
            recommendedTime = place.recommendedTime,
            recommendReason = place.recommendReason,
            imageUrl = place.imageUrl,
            kakaoPlaceUrl = place.kakaoPlaceUrl
        )
    }

    private fun toRouteResponse(route: com.dateclick.api.domain.course.entity.Route): RouteResponse {
        return RouteResponse(
            from = route.from,
            to = route.to,
            distance = route.distance,
            duration = route.duration,
            transportType = route.transportType.code,
            description = route.description
        )
    }
}
