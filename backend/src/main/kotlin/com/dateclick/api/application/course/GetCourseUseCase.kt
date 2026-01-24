package com.dateclick.api.application.course

import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.domain.course.vo.CourseId

interface GetCourseUseCase {
    suspend fun execute(courseId: CourseId): Course?
}
