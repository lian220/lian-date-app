# REST API 표준

## URL 네이밍
- 복수형 명사: `/v1/courses`, `/v1/places`
- 케밥 케이스: `/v1/course-places`
- 버전 접두사: `/v1/...`
- 동사 사용 금지: ~~`/getCourses`~~ → `/courses`

## HTTP 메서드
| 메서드 | 용도 | 응답 코드 |
|--------|------|-----------|
| GET | 조회 | 200 OK |
| POST | 생성 | 201 Created |
| PUT | 전체 수정 | 200 OK |
| PATCH | 부분 수정 | 200 OK |
| DELETE | 삭제 | 204 No Content |

## 요청/응답 DTO
```kotlin
// Request DTO - validation 포함
data class CreateCourseRequest(
    @field:NotBlank val region: String,
    @field:Min(0) val budget: Int,
    @field:NotNull val dateType: String
)

// Response DTO - domain entity와 분리
data class CourseResponse(
    val id: String,
    val region: String,
    val budget: Int,
    val places: List<PlaceResponse>,
    val createdAt: String
)
```

## 페이징
```kotlin
@GetMapping
fun list(
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "20") size: Int
): ResponseEntity<PageResponse<CourseResponse>> { ... }

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)
```

## 에러 응답 형식
```json
{
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "유효하지 않은 요청입니다",
  "details": [
    { "field": "budget", "message": "0 이상이어야 합니다" }
  ],
  "timestamp": "2024-01-01T00:00:00Z"
}
```
