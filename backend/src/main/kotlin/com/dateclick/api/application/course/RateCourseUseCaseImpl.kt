package com.dateclick.api.application.course

import com.dateclick.api.domain.course.port.outbound.CourseRepository
import com.dateclick.api.domain.rating.entity.Rating
import com.dateclick.api.domain.rating.port.RatingRepository
import com.dateclick.api.domain.rating.port.UserSatisfactionPort
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RateCourseUseCaseImpl(
    private val courseRepository: CourseRepository,
    private val ratingRepository: RatingRepository,
    private val userSatisfactionPort: UserSatisfactionPort,
) : RateCourseUseCase {
    @Transactional
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

        val savedRating =
            try {
                ratingRepository.save(rating)
            } catch (ex: DataIntegrityViolationException) {
                throw IllegalStateException("이미 평가를 완료했습니다")
            }

        // 4. Langfuse user_satisfaction 점수 전송 (비동기, fire-and-forget)
        // sessionId로 코스 생성 시의 Langfuse 트레이스와 연결
        userSatisfactionPort.sendUserSatisfactionScore(
            score = command.score,
            sessionId = command.sessionId,
            comment = "코스 ID: ${command.courseId.value}",
        )

        return savedRating
    }
}
