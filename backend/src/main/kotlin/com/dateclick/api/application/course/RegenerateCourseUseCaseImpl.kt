package com.dateclick.api.application.course

import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.domain.course.port.outbound.CourseRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RegenerateCourseUseCaseImpl(
    private val courseRepository: CourseRepository,
) : RegenerateCourseUseCase {
    override fun execute(command: RegenerateCourseCommand): Course {
        // 1. 원본 코스 조회
        val originalCourse =
            courseRepository.findById(command.originalCourseId)
                ?: throw IllegalArgumentException("Course not found: ${command.originalCourseId.value}")

        // TODO: LAD-26 (Kakao API) 완료 후 실제 구현
        // 2. 제외할 장소 목록 필터링
        // 3. 새로운 장소 검색
        // 4. AI 추천 받기
        // 5. 새 코스 생성

        // Stub: 원본 코스 정보로 새 코스 생성 (임시)
        val newCourse =
            originalCourse.regenerate(
                newPlaces = originalCourse.places, // TODO: 실제로는 새로운 장소로 교체
                newRoutes = originalCourse.routes, // TODO: 실제로는 새로운 경로 계산
            )

        return courseRepository.save(newCourse)
    }
}
