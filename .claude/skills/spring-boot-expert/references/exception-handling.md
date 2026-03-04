# 예외 처리 패턴

## 도메인 예외 계층
```kotlin
// 기본 도메인 예외 (Domain Layer)
sealed class DomainException(message: String) : RuntimeException(message)

class EntityNotFoundException(entity: String, id: Any) :
    DomainException("$entity not found: $id")

class InvalidValueException(field: String, reason: String) :
    DomainException("Invalid $field: $reason")

class BusinessRuleViolationException(rule: String) :
    DomainException("Business rule violated: $rule")
```

## 전역 예외 핸들러 (Presentation Layer)
```kotlin
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(404, "NOT_FOUND", ex.message))

    @ExceptionHandler(InvalidValueException::class)
    fun handleInvalidValue(ex: InvalidValueException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(400, "BAD_REQUEST", ex.message))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val details = ex.bindingResult.fieldErrors.map {
            FieldError(it.field, it.defaultMessage ?: "Invalid value")
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(400, "VALIDATION_ERROR", "유효하지 않은 요청", details))
    }
}

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String?,
    val details: List<FieldError>? = null,
    val timestamp: String = Instant.now().toString()
)

data class FieldError(val field: String, val message: String)
```

## 예외 흐름
```
Controller → UseCase → DomainService
    ↑                      ↓ (throw DomainException)
    └─── GlobalExceptionHandler가 catch → ErrorResponse 변환
```

## 규칙
- Domain 예외는 HTTP 상태 코드를 모르게 한다 (도메인 순수성)
- 매핑은 GlobalExceptionHandler에서만
- 예외를 삼키지 않는다 (catch 후 무시 금지)
- 외부 API 예외는 Infrastructure Layer에서 Domain 예외로 변환
