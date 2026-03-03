package com.dateclick.api.presentation.rest.health

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Health", description = "서버 상태 확인 API")
@RestController
class HealthCheckController {
    @Operation(summary = "루트 헬스체크", description = "서버 기동 상태를 확인합니다")
    @GetMapping("/")
    fun root(): ResponseEntity<HealthResponse> {
        return ResponseEntity.ok(
            HealthResponse(
                status = "UP",
                service = "date-click-api",
            ),
        )
    }

    @Operation(summary = "헬스체크", description = "서버 기동 상태를 확인합니다")
    @GetMapping("/health")
    fun healthCheck(): ResponseEntity<HealthResponse> {
        return ResponseEntity.ok(
            HealthResponse(
                status = "UP",
                service = "date-click-api",
            ),
        )
    }
}

data class HealthResponse(
    val status: String,
    val service: String,
)
