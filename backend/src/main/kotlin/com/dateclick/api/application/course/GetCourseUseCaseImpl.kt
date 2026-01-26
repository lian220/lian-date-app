package com.dateclick.api.application.course

import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.domain.course.port.outbound.CourseRepository
import com.dateclick.api.domain.course.vo.CourseId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 코스 상세 조회 유스케이스 구현체
 * 코스 ID로 저장된 코스 정보를 조회
 */
@Service
class GetCourseUseCaseImpl(
    private val courseRepository: CourseRepository,
) : GetCourseUseCase {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 코스 ID로 코스 정보를 조회합니다.
     *
     * @param courseId 조회할 코스의 ID
     * @return 코스 정보, 존재하지 않으면 null
     */
    override fun execute(courseId: CourseId): Course? {
        logger.info("Getting course: courseId={}", courseId.value)

        val course = courseRepository.findById(courseId)

        if (course == null) {
            logger.warn("Course not found: courseId={}", courseId.value)
            return null
        }

        logger.info(
            "Course found: id={}, regionName={}, placeCount={}",
            course.id.value,
            course.regionName,
            course.places.size,
        )

        return course
    }
}
