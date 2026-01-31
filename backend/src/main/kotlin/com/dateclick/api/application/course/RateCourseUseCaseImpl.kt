package com.dateclick.api.application.course

import com.dateclick.api.domain.course.port.outbound.CourseRepository
import com.dateclick.api.domain.rating.entity.Rating
import com.dateclick.api.domain.rating.port.RatingRepository
import org.springframework.stereotype.Service

@Service
class RateCourseUseCaseImpl(
    private val courseRepository: CourseRepository,
    private val ratingRepository: RatingRepository,
) : RateCourseUseCase {
    override fun execute(command: RateCourseCommand): Rating {
        // 1. 코스 존재 여부 확인
        val course =
            courseRepository.findById(command.courseId)
                ?: throw IllegalArgumentException("코스를 찾을 수 없습니다: ${command.courseId.value}")

        // 2. 중복 평가 확인
        if (ratingRepository.existsByCourseIdAndSessionId(command.courseId, command.sessionId)) {
            throw IllegalStateException("이미 평가를 완료했습니다")
        }

        // 3. 평가 생성 및 저장
        val rating =
            Rating.create(
                courseId = command.courseId,
                sessionId = command.sessionId,
                score = command.score,
            )

        return ratingRepository.save(rating)
    }
}
