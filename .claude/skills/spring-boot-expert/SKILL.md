---
name: spring-boot-expert
description: Spring Boot 3.x + Kotlin 전문가 스킬. Hexagonal Architecture, JPA, Security, REST API 패턴 가이드. Spring Boot 코드 작성, 설정, 패턴 적용 시 자동 활성화. "Controller", "Service", "Repository", "Entity", "JPA", "Security", "REST API" 키워드에 반응.
---

# Spring Boot 3.x + Kotlin Expert

## 프로젝트 스택
- Spring Boot 3.4.1 + Kotlin 1.9.25
- JVM Target: Java 21
- PostgreSQL 15 + Spring Data JPA
- Hexagonal Architecture (Ports & Adapters)

## 아키텍처 규칙

### 레이어 의존성 (절대 위반 금지)
```
Presentation → Application → Domain ← Infrastructure
(Controller)   (UseCase)   (Service, Port)  (Adapter, Repository)
```

- **Domain**: 프레임워크 어노테이션 금지 (`@Service`, `@Component` 등 불가)
- **Application**: Domain Port만 의존, Infrastructure 직접 참조 금지
- **Infrastructure**: Domain Port를 구현 (Adapter 패턴)
- **Presentation**: UseCase만 의존, Domain Service 직접 호출 금지

### 패키지 구조
```
com.dateclick.api/
├── domain/{도메인}/
│   ├── entity/          # 도메인 엔티티 (순수 Kotlin)
│   ├── vo/              # Value Objects (불변)
│   ├── port/
│   │   ├── inbound/     # UseCase 인터페이스
│   │   └── outbound/    # Repository/External Port
│   └── service/         # 도메인 서비스
├── application/{도메인}/ # UseCase 구현
├── infrastructure/
│   ├── config/          # Spring 설정
│   ├── external/        # 외부 API 클라이언트
│   └── persistence/
│       ├── entity/      # JPA 엔티티 (@Entity)
│       ├── mapper/      # Domain ↔ JPA 매퍼
│       └── repository/  # JPA Repository
└── presentation/
    ├── advice/          # 전역 예외 처리
    ├── mapper/          # Request/Response 매퍼
    └── rest/            # REST Controller
```

## 코드 패턴

### Value Object
```kotlin
// Domain VO - 불변, 유효성 검증 포함
data class Budget(val amount: Int) {
    init {
        require(amount >= 0) { "예산은 0 이상이어야 합니다" }
    }
}
```

### Domain Port
```kotlin
// Outbound Port - 도메인이 정의, 인프라가 구현
interface CourseRepositoryPort {
    fun save(course: Course): Course
    fun findById(id: CourseId): Course?
}
```

### UseCase
```kotlin
// Application Layer - Port를 통해 오케스트레이션
@Service
class CreateCourseUseCase(
    private val courseRepository: CourseRepositoryPort,
    private val placeSearchPort: PlaceSearchPort
) {
    fun execute(command: CreateCourseCommand): Course { ... }
}
```

### JPA Entity & Mapper
```kotlin
// Infrastructure - JPA 엔티티 (Domain Entity와 분리)
@Entity
@Table(name = "courses")
class JpaCourseEntity(
    @Id val id: UUID,
    val region: String,
    val budget: Int
)

// Mapper - Domain ↔ JPA 변환
object CourseMapper {
    fun toDomain(jpa: JpaCourseEntity): Course = ...
    fun toJpa(domain: Course): JpaCourseEntity = ...
}
```

### Controller
```kotlin
@RestController
@RequestMapping("/v1/courses")
class CourseController(private val createCourseUseCase: CreateCourseUseCase) {

    @PostMapping
    fun create(@Valid @RequestBody request: CreateCourseRequest): ResponseEntity<CourseResponse> {
        val command = RequestMapper.toCommand(request)
        val course = createCourseUseCase.execute(command)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseMapper.toResponse(course))
    }
}
```

## 상세 가이드

- **REST API 표준**: [references/rest-api-standards.md](references/rest-api-standards.md)
- **JPA 패턴**: [references/jpa-patterns.md](references/jpa-patterns.md)
- **예외 처리**: [references/exception-handling.md](references/exception-handling.md)
