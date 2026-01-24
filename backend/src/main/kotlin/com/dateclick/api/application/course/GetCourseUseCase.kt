package com.dateclick.api.application.course

import com.dateclick.api.domain.course.entity.Course
import com.dateclick.api.domain.course.vo.CourseId

interface GetCourseUseCase {
    fun execute(courseId: CourseId): Course?
}
