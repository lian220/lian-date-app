package com.dateclick.api.presentation.rest.health

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/health")
class HealthCheckController {
    @GetMapping
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
