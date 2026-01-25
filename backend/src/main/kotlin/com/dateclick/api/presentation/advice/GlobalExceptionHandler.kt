package com.dateclick.api.presentation.advice

import com.dateclick.api.infrastructure.ratelimit.RateLimitException
import com.dateclick.api.presentation.rest.common.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val message = ex.bindingResult.fieldErrors.firstOrNull()?.defaultMessage
            ?: "잘못된 요청입니다"
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("INVALID_REQUEST", message))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("INVALID_REQUEST", ex.message ?: "잘못된 요청입니다"))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFoundException(ex: NoSuchElementException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("NOT_FOUND", ex.message ?: "리소스를 찾을 수 없습니다"))
    }

    @ExceptionHandler(RateLimitException::class)
    fun handleRateLimitException(ex: RateLimitException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Rate limit exceeded: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .header("Retry-After", ex.retryAfterSeconds.toString())
            .body(
                ApiResponse.error(
                    "RATE_LIMIT_EXCEEDED",
                    "요청 횟수 제한을 초과했습니다. ${ex.retryAfterSeconds}초 후 다시 시도해주세요."
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Unexpected error occurred", ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다"))
    }
}
