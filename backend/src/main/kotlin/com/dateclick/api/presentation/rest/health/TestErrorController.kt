package com.dateclick.api.presentation.rest.health

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
@ConditionalOnProperty(name = ["test.endpoints.enabled"], havingValue = "true", matchIfMissing = false)
class TestErrorController {

    @GetMapping("/error/backend")
    fun triggerBackendError(): Nothing {
        throw RuntimeException("[Sentry 테스트] 백엔드 에러 강제 발생")
    }
}
