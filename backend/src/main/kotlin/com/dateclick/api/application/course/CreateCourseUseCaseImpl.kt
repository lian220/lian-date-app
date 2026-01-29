package com.dateclick.api.application.course

import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.domain.course.entity.CoursePlace
import com.dateclick.api.domain.course.port.outbound.AiGenerationPort
import com.dateclick.api.domain.course.port.outbound.CourseRepository
import com.dateclick.api.domain.course.vo.EstimatedCost
import com.dateclick.api.domain.place.vo.PlaceId
import com.dateclick.api.domain.region.port.RegionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 코스 생성 유스케이스 구현체
 * AI 기반 데이트 코스 추천 및 생성
 * MVP: AI가 장소 정보를 포함한 전체 코스를 생성
 */
@Service
class CreateCourseUseCaseImpl(
    private val regionRepository: RegionRepository,
    private val aiGenerationPort: AiGenerationPort,
    private val courseRepository: CourseRepository,
) : CreateCourseUseCase {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun execute(command: CreateCourseCommand): Course {
        logger.info(
            "Creating course: regionId={}, dateType={}, budget={}, sessionId={}",
            command.regionId.value,
            command.dateType,
            command.budget.toDisplayName(),
            command.sessionId,
        )

        // 1. 지역 정보 조회
        val region =
            regionRepository.findById(command.regionId)
                ?: throw IllegalArgumentException("Region not found: ${command.regionId.value}")

        logger.debug("Found region: {}", region.name)

        // 2. AI 코스 생성 (AI가 장소 정보를 포함한 전체 코스를 생성)
        val aiRecommendation =
            aiGenerationPort.generateCourseRecommendation(
                region = region,
                dateType = command.dateType,
                budget = command.budget,
                specialRequest = command.specialRequest,
            )

        logger.info("AI recommended {} places", aiRecommendation.places.size)

        // 3. AI 추천 결과를 CoursePlace로 변환
        val coursePlaces =
            aiRecommendation.places.map { aiPlace ->
                CoursePlace(
                    order = aiPlace.order,
                    placeId = PlaceId("ai-${command.sessionId}-${aiPlace.order}"),
                    name = aiPlace.name,
                    category = aiPlace.category,
                    categoryDetail = aiPlace.categoryDetail,
                    address = aiPlace.address,
                    roadAddress = aiPlace.roadAddress,
                    location = aiPlace.location,
                    phone = aiPlace.phone,
                    estimatedCost = EstimatedCost(aiPlace.estimatedCost),
                    estimatedDuration = aiPlace.estimatedDuration,
                    recommendedTime = aiPlace.recommendedTime,
                    recommendReason = aiPlace.recommendReason,
                    imageUrl = null,
                    kakaoPlaceUrl = "",
                )
            }

        // 4. 장소 간 경로 계산 (간단한 기본값 사용)
        val routes = calculateRoutes(coursePlaces)
        logger.debug("Calculated {} routes", routes.size)

        // 6. Course 엔티티 생성
        val course =
            Course.create(
                regionId = command.regionId,
                regionName = region.name,
                dateType = command.dateType,
                budget = command.budget,
                places = coursePlaces,
                routes = routes,
                sessionId = command.sessionId,
            )

        // 7. 저장
        val savedCourse = courseRepository.save(course)

        logger.info(
            "Course created successfully: id={}, totalCost={}, placeCount={}",
            savedCourse.id.value,
            savedCourse.totalEstimatedCost.value,
            savedCourse.places.size,
        )

        return savedCourse
    }

    /**
     * 장소 간 경로 계산 (MVP: 간단한 거리 계산 사용)
     */
    private fun calculateRoutes(places: List<CoursePlace>): List<com.dateclick.api.domain.course.entity.Route> {
        if (places.size < 2) {
            return emptyList()
        }

        return places.zipWithNext { from, to ->
            val distance = calculateDistance(from.location, to.location)
            com.dateclick.api.domain.course.entity.Route(
                from = from.order,
                to = to.order,
                distance = distance.toInt(),
                duration = estimateDuration(distance.toDouble()),
                transportType =
                    if (distance < 1000) {
                        com.dateclick.api.domain.course.entity.TransportType.WALK
                    } else {
                        com.dateclick.api.domain.course.entity.TransportType.TRANSIT
                    },
                description = "${from.name}에서 ${to.name}까지",
            )
        }
    }

    /**
     * Haversine formula를 사용한 두 지점 간 거리 계산 (미터 단위)
     */
    private fun calculateDistance(
        from: com.dateclick.api.domain.place.vo.Location,
        to: com.dateclick.api.domain.place.vo.Location,
    ): Double {
        val earthRadius = 6371000.0 // 지구 반지름 (미터)
        val dLat = Math.toRadians(to.lat - from.lat)
        val dLon = Math.toRadians(to.lng - from.lng)

        val a =
            Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(from.lat)) * Math.cos(Math.toRadians(to.lat)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return earthRadius * c
    }

    /**
     * 거리 기반 예상 소요 시간 계산 (분 단위)
     */
    private fun estimateDuration(distanceInMeters: Double): Int {
        return when {
            distanceInMeters < 500 -> 5 // 500m 미만: 도보 5분
            distanceInMeters < 1000 -> 10 // 1km 미만: 도보 10분
            distanceInMeters < 2000 -> 15 // 2km 미만: 도보 15분
            else -> (distanceInMeters / 400).toInt().coerceAtLeast(20) // 대중교통 기준 (시속 24km)
        }
    }
}
