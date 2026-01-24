# 제품 요구사항 명세서 (PRD)
## '데이트 딸깍' MVP

---

| 항목 | 내용 |
|------|------|
| **문서 버전** | v1.0 |
| **작성일** | 2025년 1월 24일 |
| **작성자** | Product Team |
| **상태** | Draft → Review 대기 |
| **대상 독자** | 개발팀, 디자인팀, QA팀 |

---

## 1. 배경 및 문제 정의

### 1.1 왜 이 서비스를 만드는가?

**시장 기회**

대한민국 Z세대 커플들은 데이트 계획 수립에 상당한 시간과 에너지를 소모하고 있다. 네이버 블로그, 인스타그램, 유튜브, 카카오맵 등 다양한 플랫폼에 정보가 분산되어 있어, 하나의 데이트 코스를 완성하기 위해 평균 1~2시간의 검색 시간이 필요하다.

**핵심 문제**

| 문제 | 현재 상황 | 영향 |
|------|----------|------|
| 정보 분산 | 5개 이상 플랫폼에서 개별 검색 필요 | 시간 낭비, 피로 누적 |
| 코스 구성 어려움 | 장소 간 동선, 시간 배분을 직접 계획 | 계획 실패 시 데이트 만족도 하락 |
| 정보 신뢰성 부족 | 오래된 리뷰, 폐업 정보 미반영 | 기대와 현실의 괴리 |
| 매너리즘 | 늘 가던 곳만 반복 | 권태기 가속화 |

**우리의 솔루션**

'데이트 딸깍'은 사용자가 `지역`, `데이트 유형`, `예산` 세 가지만 입력하면 AI가 최적화된 데이트 코스를 즉시 추천하는 서비스다. "딸깍" 한 번으로 데이트 계획이 완성된다.

---

## 2. 타겟 유저 및 페르소나

### 2.1 Primary Persona: 플래너형 직장인 "김도윤"

| 항목 | 내용 |
|------|------|
| 나이/직업 | 27세 / IT 스타트업 개발자 (3년차) |
| 거주지 | 서울 마포구 |
| 연애 상태 | 2년차 연인과 교제 중 |
| 월 데이트 예산 | 30~50만원 |

**핵심 니즈**: 시간 절약 & 새로움

**주요 Pain Point**
- 평일 야근으로 데이트 계획 세울 시간 부족
- 정보 검색에 1~2시간 소요되는 것이 비효율적
- 2년차 매너리즘으로 새로운 코스가 필요함

**기대하는 가치**
- "금요일 퇴근 후 5분 만에 주말 데이트 코스 확정"
- "안 가본 곳 위주로 추천받아 새로운 경험"

### 2.2 Secondary Persona: 설렘 추구형 대학생 "이서아"

| 항목 | 내용 |
|------|------|
| 나이/직업 | 23세 / 대학교 3학년 + 카페 알바 |
| 거주지 | 경기도 수원시 |
| 연애 상태 | 썸 시작 3주차 |
| 월 데이트 예산 | 15~25만원 |

**핵심 니즈**: 센스 증명 & 가성비

**주요 Pain Point**
- 데이트 경험 부족으로 코스 구성에 자신 없음
- 학생이라 예산 제한이 있지만 "싸 보이면" 안 됨
- 수원 거주로 서울 핫플 접근성 및 로컬 정보 부족

**기대하는 가치**
- "AI가 추천해준 검증된 코스로 자신감 획득"
- "15만원으로도 감성 있는 데이트 가능"

---

## 3. 제품 목표 및 성공 지표

### 3.1 MVP 목표

> **"사용자가 3분 내에 만족스러운 데이트 코스를 생성하고, 이를 실제 데이트에 활용하게 만든다"**

### 3.2 성공 지표 (HEART Framework)

| HEART | 지표명 | 정의 | MVP 목표 | 측정 방법 |
|-------|--------|------|----------|-----------|
| **Happiness** | 코스 만족도 | 추천 코스에 대한 사용자 평점 | ≥ 4.0/5.0 | 코스 생성 후 5점 척도 설문 |
| **Happiness** | NPS | 서비스 추천 의향 | ≥ 30 | 0-10점 NPS 설문 |
| **Engagement** | 장소 클릭률 (CTR) | 코스 내 개별 장소 상세 클릭 비율 | ≥ 60% | (클릭된 장소 수 / 총 추천 장소 수) × 100 |
| **Engagement** | 코스 저장률 | 생성된 코스 중 저장 비율 | ≥ 40% | (저장 코스 / 총 생성 코스) × 100 |
| **Adoption** | 첫 코스 생성 전환율 | 첫 접속 후 코스 생성 완료 비율 | ≥ 70% | (코스 생성 완료 세션 / 신규 세션) × 100 |
| **Retention** | D7 Retention | 첫 접속 7일 후 재방문율 | ≥ 25% | 코호트 분석 (익명 세션 ID 기반) |
| **Task Success** | 코스 생성 시간 | 입력 시작~결과 확인 소요 시간 | ≤ 3분 | 이벤트 타임스탬프 차이 |

> **Note**: 비로그인 MVP이므로 브라우저 익명 세션 ID 기반으로 측정

### 3.3 MVP 기간 핵심 Focus 지표 (North Star)

1. **코스 만족도 ≥ 4.0** → 제품 품질 검증
2. **첫 코스 생성 전환율 ≥ 70%** → 진입 장벽 최소화 검증
3. **D7 Retention ≥ 25%** → PMF 검증

---

## 4. MVP 기능 명세

### 4.1 기능 개요

```
┌─────────────────────────────────────────────────────────────────┐
│                        데이트 딸깍 MVP                          │
├─────────────────────────────────────────────────────────────────┤
│  F1. 데이트 조건 입력 (지역/유형/예산)                           │
│  F2. AI 맞춤형 코스 추천                                         │
│  F3. 코스 상세 보기 및 장소 정보                                 │
│  F4. 코스 저장 (로컬 스토리지)                                   │
│  F5. 코스 만족도 평가                                            │
└─────────────────────────────────────────────────────────────────┘
```

#### 로그인 정책

> **MVP 원칙: 로그인 없이 모든 메인 기능 즉시 사용 가능**

**✅ MVP 비로그인 기능 체크리스트**

| 기능 | 비로그인 사용 | 구현 방식 | 비고 |
|------|:-------------:|----------|------|
| F1. 조건 입력 | ✅ | - | 즉시 사용 |
| F2. 코스 추천 | ✅ | 익명 세션 기반 | Rate Limit 적용 |
| F3. 코스 상세/지도 | ✅ | - | 카카오맵 연동 |
| F4. 코스 저장 | ✅ | 브라우저 로컬 스토리지 | 최대 20개, 기기별 저장 |
| F5. 만족도 평가 | ✅ | 익명 세션 ID | 개인 식별 불가 |

**🔐 향후 로그인 필요 기능 (v1.1+)**

| 기능 | 로그인 필요 사유 | 예정 버전 |
|------|-----------------|----------|
| 클라우드 코스 동기화 | 기기 간 데이터 연동 | v1.1 |
| 개인화 추천 | 사용자 취향 학습 | v1.1 |
| 코스 히스토리 | 과거 이용 코스 기록 | v1.1 |
| 카카오톡 공유 | 발신자 정보 필요 | v1.1 |
| 찜한 장소 관리 | 개인 데이터 저장 | v1.2 |
| 리뷰 작성 | 작성자 식별 필요 | v1.2 |

**로그인 유도 시점 (v1.1 이후)**

```
[로그인 유도 트리거]
1. 코스 저장 20개 초과 시
   → "로그인하면 무제한으로 저장할 수 있어요"

2. 내 코스 화면 진입 시
   → "로그인하면 어디서든 내 코스를 볼 수 있어요"

3. 동일 조건 3회 이상 검색 시
   → "로그인하면 취향에 맞는 코스를 추천받을 수 있어요"

4. 공유 버튼 탭 시
   → "로그인하고 친구에게 코스를 공유해보세요"
```

### 4.2 기술 스택

| 영역 | 기술 | 비고 |
|------|------|------|
| **Frontend** | Next.js 14+ (App Router) | TypeScript, Tailwind CSS |
| **Backend** | Kotlin + Spring Boot 3.x | Java 21, Coroutines, DDD Clean Architecture |
| **Database** | PostgreSQL | 코스 데이터, 평가 통계 |
| **지도/장소** | Kakao Maps API, Kakao Local API | 장소 검색, 지도 표시 |
| **AI 추천** | OpenAI API (GPT-4) | 코스 생성 로직 |
| **인프라** | AWS / GCP (TBD) | EC2/Cloud Run, RDS |

> **Note**: 로그인 기능은 MVP 범위 외. v1.1에서 Kakao OAuth 2.0 도입 예정

---

### 4.2.1 Backend Architecture (DDD Clean Architecture)

#### 아키텍처 원칙

```
┌─────────────────────────────────────────────────────────────────┐
│                      Presentation Layer                         │
│                   (Controllers, DTOs, Mappers)                  │
├─────────────────────────────────────────────────────────────────┤
│                      Application Layer                          │
│                (Use Cases, Application Services)                │
├─────────────────────────────────────────────────────────────────┤
│                        Domain Layer                             │
│        (Entities, Value Objects, Domain Services, Ports)        │
├─────────────────────────────────────────────────────────────────┤
│                     Infrastructure Layer                        │
│          (Repositories, External APIs, Persistence)            │
└─────────────────────────────────────────────────────────────────┘

의존성 방향: Presentation → Application → Domain ← Infrastructure
```

#### 패키지 구조

```
com.dateclick.api/
├── presentation/                    # Presentation Layer
│   ├── rest/
│   │   ├── course/
│   │   │   ├── CourseController.kt
│   │   │   ├── CourseRequest.kt
│   │   │   └── CourseResponse.kt
│   │   ├── region/
│   │   │   └── RegionController.kt
│   │   └── place/
│   │       └── PlaceController.kt
│   ├── advice/
│   │   └── GlobalExceptionHandler.kt
│   └── mapper/
│       └── CourseDtoMapper.kt
│
├── application/                     # Application Layer
│   ├── course/
│   │   ├── CreateCourseUseCase.kt
│   │   ├── GetCourseUseCase.kt
│   │   ├── RegenerateCourseUseCase.kt
│   │   └── RateCourseUseCase.kt
│   ├── region/
│   │   └── GetRegionsUseCase.kt
│   └── place/
│       └── GetPlaceDetailUseCase.kt
│
├── domain/                          # Domain Layer (핵심)
│   ├── course/
│   │   ├── entity/
│   │   │   ├── Course.kt
│   │   │   ├── CoursePlace.kt
│   │   │   └── Route.kt
│   │   ├── vo/                      # Value Objects
│   │   │   ├── CourseId.kt
│   │   │   ├── Budget.kt
│   │   │   ├── DateType.kt
│   │   │   └── EstimatedCost.kt
│   │   ├── service/
│   │   │   └── CourseGenerationService.kt
│   │   └── port/                    # Ports (Interface)
│   │       ├── outbound/
│   │       │   ├── CourseRepository.kt
│   │       │   ├── PlaceSearchPort.kt
│   │       │   └── AiGenerationPort.kt
│   │       └── inbound/
│   │           └── CourseUseCase.kt
│   ├── region/
│   │   ├── entity/
│   │   │   └── Region.kt
│   │   ├── vo/
│   │   │   └── RegionId.kt
│   │   └── port/
│   │       └── RegionRepository.kt
│   ├── place/
│   │   ├── entity/
│   │   │   └── Place.kt
│   │   └── vo/
│   │       ├── PlaceId.kt
│   │       ├── Location.kt
│   │       └── BusinessHours.kt
│   └── rating/
│       ├── entity/
│       │   └── Rating.kt
│       └── port/
│           └── RatingRepository.kt
│
└── infrastructure/                  # Infrastructure Layer
    ├── persistence/
    │   ├── entity/
    │   │   ├── CourseJpaEntity.kt
    │   │   ├── RegionJpaEntity.kt
    │   │   └── RatingJpaEntity.kt
    │   ├── repository/
    │   │   ├── CourseJpaRepository.kt
    │   │   ├── CourseRepositoryImpl.kt
    │   │   └── RatingRepositoryImpl.kt
    │   └── mapper/
    │       └── CourseEntityMapper.kt
    ├── external/
    │   ├── kakao/
    │   │   ├── KakaoLocalClient.kt
    │   │   ├── KakaoPlaceSearchAdapter.kt  # PlaceSearchPort 구현
    │   │   └── dto/
    │   │       ├── KakaoPlaceResponse.kt
    │   │       └── KakaoRouteResponse.kt
    │   └── openai/
    │       ├── OpenAiClient.kt
    │       ├── OpenAiGenerationAdapter.kt  # AiGenerationPort 구현
    │       └── dto/
    │           └── OpenAiChatRequest.kt
    └── config/
        ├── JpaConfig.kt
        ├── WebClientConfig.kt
        └── OpenAiConfig.kt
```

#### Domain Layer 상세

**Core Entities**

```kotlin
// Course Entity (Aggregate Root)
@Entity
class Course private constructor(
    val id: CourseId,
    val regionId: RegionId,
    val dateType: DateType,
    val budget: Budget,
    val places: List<CoursePlace>,
    val routes: List<Route>,
    val createdAt: Instant
) {
    val totalEstimatedCost: EstimatedCost
        get() = EstimatedCost(places.sumOf { it.estimatedCost.value })
    
    fun regenerate(newPlaces: List<CoursePlace>, newRoutes: List<Route>): Course
    
    companion object {
        fun create(
            regionId: RegionId,
            dateType: DateType,
            budget: Budget,
            places: List<CoursePlace>,
            routes: List<Route>
        ): Course
    }
}

// Value Objects
@JvmInline
value class CourseId(val value: String) {
    init {
        require(value.startsWith("course_")) { "Invalid course ID format" }
    }
}

@JvmInline
value class Budget(val value: IntRange) {
    companion object {
        fun from(budgetString: String): Budget = when(budgetString) {
            "0-30000" -> Budget(0..30000)
            "30000-50000" -> Budget(30000..50000)
            "50000-100000" -> Budget(50000..100000)
            "100000-" -> Budget(100000..Int.MAX_VALUE)
            else -> throw IllegalArgumentException("Invalid budget: $budgetString")
        }
    }
}

enum class DateType(val displayName: String) {
    ROMANTIC("감성/로맨틱"),
    ACTIVITY("액티비티"),
    FOOD("맛집 탐방"),
    CULTURE("문화/예술"),
    HEALING("힐링")
}
```

**Ports (Interfaces)**

```kotlin
// Outbound Port - Repository
interface CourseRepository {
    suspend fun save(course: Course): Course
    suspend fun findById(id: CourseId): Course?
    suspend fun findBySessionId(sessionId: String): List<Course>
}

// Outbound Port - External API
interface PlaceSearchPort {
    suspend fun searchPlaces(
        regionId: RegionId,
        category: PlaceCategory,
        limit: Int
    ): List<Place>
    
    suspend fun getPlaceDetail(placeId: PlaceId): Place?
    
    suspend fun calculateRoute(from: Location, to: Location): Route
}

interface AiGenerationPort {
    suspend fun generateCourseRecommendation(
        region: Region,
        dateType: DateType,
        budget: Budget,
        specialRequest: String?,
        candidatePlaces: List<Place>
    ): AiCourseRecommendation
}
```

**Use Cases (Application Layer)**

```kotlin
@Service
class CreateCourseUseCase(
    private val courseRepository: CourseRepository,
    private val regionRepository: RegionRepository,
    private val placeSearchPort: PlaceSearchPort,
    private val aiGenerationPort: AiGenerationPort,
    private val courseGenerationService: CourseGenerationService
) {
    suspend fun execute(command: CreateCourseCommand): Course {
        // 1. Region 조회
        val region = regionRepository.findById(command.regionId)
            ?: throw RegionNotFoundException(command.regionId)
        
        // 2. 후보 장소 검색 (카카오 API)
        val candidatePlaces = placeSearchPort.searchPlaces(
            regionId = command.regionId,
            category = PlaceCategory.ALL,
            limit = 50
        )
        
        // 3. AI 코스 추천 요청
        val aiRecommendation = aiGenerationPort.generateCourseRecommendation(
            region = region,
            dateType = command.dateType,
            budget = command.budget,
            specialRequest = command.specialRequest,
            candidatePlaces = candidatePlaces
        )
        
        // 4. 코스 생성 (Domain Service)
        val course = courseGenerationService.createCourse(
            regionId = command.regionId,
            dateType = command.dateType,
            budget = command.budget,
            recommendation = aiRecommendation
        )
        
        // 5. 저장 및 반환
        return courseRepository.save(course)
    }
}

data class CreateCourseCommand(
    val regionId: RegionId,
    val dateType: DateType,
    val budget: Budget,
    val specialRequest: String?,
    val sessionId: String
)
```

#### Infrastructure Layer 상세

**Repository Implementation**

```kotlin
@Repository
class CourseRepositoryImpl(
    private val courseJpaRepository: CourseJpaRepository,
    private val entityMapper: CourseEntityMapper
) : CourseRepository {
    
    override suspend fun save(course: Course): Course {
        val entity = entityMapper.toJpaEntity(course)
        val saved = courseJpaRepository.save(entity)
        return entityMapper.toDomain(saved)
    }
    
    override suspend fun findById(id: CourseId): Course? {
        return courseJpaRepository.findById(id.value)
            .map { entityMapper.toDomain(it) }
            .orElse(null)
    }
}
```

**External API Adapter**

```kotlin
@Component
class KakaoPlaceSearchAdapter(
    private val kakaoLocalClient: KakaoLocalClient
) : PlaceSearchPort {
    
    override suspend fun searchPlaces(
        regionId: RegionId,
        category: PlaceCategory,
        limit: Int
    ): List<Place> {
        val response = kakaoLocalClient.searchByKeyword(
            query = category.toKakaoQuery(),
            x = regionId.centerLng,
            y = regionId.centerLat,
            radius = 3000,
            size = limit
        )
        
        return response.documents.map { doc ->
            Place(
                id = PlaceId("kakao_${doc.id}"),
                name = doc.placeName,
                category = PlaceCategory.from(doc.categoryGroupCode),
                location = Location(doc.y.toDouble(), doc.x.toDouble()),
                address = doc.addressName,
                roadAddress = doc.roadAddressName,
                phone = doc.phone.takeIf { it.isNotBlank() }
            )
        }
    }
}
```

#### 레이어별 의존성 규칙

| From Layer | Can Depend On | Cannot Depend On |
|------------|---------------|------------------|
| **Presentation** | Application, Domain | Infrastructure |
| **Application** | Domain | Presentation, Infrastructure |
| **Domain** | 없음 (Pure Kotlin) | 모든 외부 레이어 |
| **Infrastructure** | Domain | Application, Presentation |

#### 테스트 전략

```
test/
├── unit/
│   ├── domain/           # 순수 단위 테스트 (Mocking 없음)
│   │   ├── CourseTest.kt
│   │   └── BudgetTest.kt
│   └── application/      # UseCase 테스트 (Port Mocking)
│       └── CreateCourseUseCaseTest.kt
├── integration/
│   ├── persistence/      # Repository 통합 테스트
│   │   └── CourseRepositoryTest.kt
│   └── external/         # 외부 API 통합 테스트
│       └── KakaoPlaceSearchAdapterTest.kt
└── e2e/
    └── CourseApiE2ETest.kt
```

---

### 4.3 API 명세

#### Base URL
```
Production: https://api.date-click.kr/v1
Development: http://localhost:8080/v1
```

#### 공통 Request Headers
| Header | Required | Description |
|--------|----------|-------------|
| `X-Session-Id` | Y | 익명 세션 ID (UUID v4, FE에서 생성 및 로컬 저장) |
| `Content-Type` | Y | `application/json` |

> **Note**: 비로그인 MVP이므로 Authorization 헤더 불필요. 세션 ID는 Rate Limiting 및 통계 목적으로만 사용.

#### 공통 Response 형식
```json
{
  "success": true,
  "data": { ... },
  "error": null
}
```

#### 공통 Error Response 형식
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "message": "사용자에게 보여줄 메시지"
  }
}
```

#### 공통 Error Codes
| Code | HTTP Status | 설명 |
|------|-------------|------|
| `INVALID_REQUEST` | 400 | 요청 파라미터 오류 |
| `NOT_FOUND` | 404 | 리소스를 찾을 수 없음 |
| `EXTERNAL_API_ERROR` | 502 | 외부 API (카카오, OpenAI) 오류 |
| `INTERNAL_ERROR` | 500 | 서버 내부 오류 |
| `RATE_LIMIT_EXCEEDED` | 429 | 요청 횟수 초과 |

---

#### API 1: 권역 목록 조회

지원하는 권역(지역) 목록을 조회합니다.

**Endpoint**
```
GET /regions
```

**Query Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `city` | string | N | 필터링할 도시 (`seoul`, `gyeonggi`) |

**Response**
```json
{
  "success": true,
  "data": {
    "regions": [
      {
        "id": "hongdae",
        "name": "홍대/합정",
        "city": "seoul",
        "description": "인디 감성, 클럽/펍, 젊은 상권",
        "keywords": ["인디", "클럽", "카페"],
        "centerLat": 37.5563,
        "centerLng": 126.9236
      },
      {
        "id": "seongsu",
        "name": "성수/서울숲",
        "city": "seoul",
        "description": "힙플레이스, 카페, 갤러리",
        "keywords": ["힙플", "카페", "갤러리"],
        "centerLat": 37.5447,
        "centerLng": 127.0557
      }
    ]
  },
  "error": null
}
```

---

#### API 2: 코스 생성

입력된 조건을 기반으로 AI가 데이트 코스를 생성합니다.

**Endpoint**
```
POST /courses
```

**Request Body**
```json
{
  "regionId": "seongsu",
  "dateType": "romantic",
  "budget": "30000-50000",
  "specialRequest": "사진 찍기 좋은 곳 위주로"
}
```

**Request Body Fields**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `regionId` | string | Y | 권역 ID (API 1에서 조회) |
| `dateType` | string | Y | 데이트 유형 (하단 enum 참조) |
| `budget` | string | Y | 예산 범위 (하단 enum 참조) |
| `specialRequest` | string | N | 특별 요청사항 (최대 100자) |

**dateType Enum**
| Value | 표시명 |
|-------|--------|
| `romantic` | 감성/로맨틱 |
| `activity` | 액티비티 |
| `food` | 맛집 탐방 |
| `culture` | 문화/예술 |
| `healing` | 힐링 |

**budget Enum**
| Value | 표시명 |
|-------|--------|
| `0-30000` | ~3만원 |
| `30000-50000` | 3~5만원 |
| `50000-100000` | 5~10만원 |
| `100000-` | 10만원~ |

**Response (Success)**
```json
{
  "success": true,
  "data": {
    "courseId": "course_abc123",
    "regionId": "seongsu",
    "regionName": "성수/서울숲",
    "dateType": "romantic",
    "budget": "30000-50000",
    "totalEstimatedCost": 45000,
    "places": [
      {
        "order": 1,
        "placeId": "kakao_12345678",
        "name": "어니언 성수",
        "category": "카페",
        "categoryDetail": "베이커리카페",
        "address": "서울 성동구 아차산로9길 8",
        "roadAddress": "서울 성동구 성수동2가 277-17",
        "lat": 37.5447123,
        "lng": 127.0557456,
        "phone": "02-1234-5678",
        "estimatedCost": 12000,
        "estimatedDuration": 90,
        "recommendedTime": "14:00",
        "recommendReason": "성수동 대표 베이커리 카페로, 빈티지한 인테리어가 사진 찍기 좋아요",
        "imageUrl": "https://place.kakao.com/...",
        "kakaoPlaceUrl": "https://place.map.kakao.com/12345678"
      },
      {
        "order": 2,
        "placeId": "kakao_23456789",
        "name": "서울숲",
        "category": "액티비티",
        "categoryDetail": "공원",
        "address": "서울 성동구 뚝섬로 273",
        "roadAddress": "서울 성동구 성수동1가 685",
        "lat": 37.5443215,
        "lng": 127.0374568,
        "phone": null,
        "estimatedCost": 0,
        "estimatedDuration": 120,
        "recommendedTime": "16:00",
        "recommendReason": "도심 속 자연을 즐기며 산책하기 좋은 공원이에요",
        "imageUrl": "https://place.kakao.com/...",
        "kakaoPlaceUrl": "https://place.map.kakao.com/23456789"
      },
      {
        "order": 3,
        "placeId": "kakao_34567890",
        "name": "뚝섬회관",
        "category": "음식점",
        "categoryDetail": "한식",
        "address": "서울 성동구 성수이로 88",
        "roadAddress": "서울 성동구 성수동2가 315-20",
        "lat": 37.5421456,
        "lng": 127.0512345,
        "phone": "02-2345-6789",
        "estimatedCost": 33000,
        "estimatedDuration": 90,
        "recommendedTime": "18:30",
        "recommendReason": "성수동 로컬 맛집으로 푸짐한 한식을 즐길 수 있어요",
        "imageUrl": "https://place.kakao.com/...",
        "kakaoPlaceUrl": "https://place.map.kakao.com/34567890"
      }
    ],
    "routes": [
      {
        "from": 1,
        "to": 2,
        "distance": 850,
        "duration": 12,
        "transportType": "walk",
        "description": "도보 12분"
      },
      {
        "from": 2,
        "to": 3,
        "distance": 620,
        "duration": 9,
        "transportType": "walk",
        "description": "도보 9분"
      }
    ],
    "createdAt": "2025-01-24T14:30:00Z"
  },
  "error": null
}
```

**Response Fields - places[]**
| Field | Type | Description |
|-------|------|-------------|
| `order` | number | 순서 (1, 2, 3) |
| `placeId` | string | 카카오 장소 ID (kakao_ prefix) |
| `name` | string | 장소명 |
| `category` | string | 대분류 (카페, 음식점, 액티비티) |
| `categoryDetail` | string | 소분류 |
| `address` | string | 지번 주소 |
| `roadAddress` | string | 도로명 주소 |
| `lat` | number | 위도 |
| `lng` | number | 경도 |
| `phone` | string? | 전화번호 (nullable) |
| `estimatedCost` | number | 예상 비용 (1인, 원) |
| `estimatedDuration` | number | 예상 체류 시간 (분) |
| `recommendedTime` | string | 추천 방문 시간 (HH:mm) |
| `recommendReason` | string | AI 추천 이유 |
| `imageUrl` | string? | 대표 이미지 URL (nullable) |
| `kakaoPlaceUrl` | string | 카카오맵 장소 URL |

**Response Fields - routes[]**
| Field | Type | Description |
|-------|------|-------------|
| `from` | number | 출발 장소 order |
| `to` | number | 도착 장소 order |
| `distance` | number | 거리 (미터) |
| `duration` | number | 소요 시간 (분) |
| `transportType` | string | 이동 수단 (`walk`, `transit`, `car`) |
| `description` | string | 이동 설명 |

**Error Response**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_REQUEST",
    "message": "지역을 선택해주세요"
  }
}
```

---

#### API 3: 코스 상세 조회

생성된 코스의 상세 정보를 조회합니다.

**Endpoint**
```
GET /courses/{courseId}
```

**Path Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `courseId` | string | Y | 코스 ID |

**Response**

API 2의 Response와 동일한 구조

**Error Response**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "NOT_FOUND",
    "message": "코스를 찾을 수 없습니다"
  }
}
```

---

#### API 4: 코스 재생성

동일 조건으로 새로운 코스를 생성합니다.

**Endpoint**
```
POST /courses/{courseId}/regenerate
```

**Path Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `courseId` | string | Y | 기존 코스 ID |

**Request Body**
```json
{
  "excludePlaceIds": ["kakao_12345678"]
}
```

**Request Body Fields**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `excludePlaceIds` | string[] | N | 제외할 장소 ID 목록 |

**Response**

API 2의 Response와 동일한 구조 (새로운 courseId 발급)

---

#### API 5: 만족도 평가 제출

코스에 대한 만족도 평가를 제출합니다.

**Endpoint**
```
POST /courses/{courseId}/ratings
```

**Path Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `courseId` | string | Y | 코스 ID |

**Request Body**
```json
{
  "score": 4,
  "sessionId": "session_xyz789"
}
```

**Request Body Fields**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `score` | number | Y | 평점 (1~5) |
| `sessionId` | string | Y | 익명 세션 ID (FE에서 생성) |

**Response**
```json
{
  "success": true,
  "data": {
    "ratingId": "rating_def456",
    "courseId": "course_abc123",
    "score": 4,
    "createdAt": "2025-01-24T15:00:00Z"
  },
  "error": null
}
```

**Error Response**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_REQUEST",
    "message": "이미 평가를 완료했습니다"
  }
}
```

---

#### API 6: 장소 상세 조회 (카카오 프록시)

카카오 장소의 상세 정보를 조회합니다.

**Endpoint**
```
GET /places/{placeId}
```

**Path Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `placeId` | string | Y | 카카오 장소 ID (kakao_XXXXXXXX) |

**Response**
```json
{
  "success": true,
  "data": {
    "placeId": "kakao_12345678",
    "name": "어니언 성수",
    "category": "카페",
    "categoryDetail": "베이커리카페",
    "address": "서울 성동구 아차산로9길 8",
    "roadAddress": "서울 성동구 성수동2가 277-17",
    "lat": 37.5447123,
    "lng": 127.0557456,
    "phone": "02-1234-5678",
    "businessHours": [
      { "day": "월", "open": "11:00", "close": "22:00" },
      { "day": "화", "open": "11:00", "close": "22:00" },
      { "day": "수", "open": "11:00", "close": "22:00" },
      { "day": "목", "open": "11:00", "close": "22:00" },
      { "day": "금", "open": "11:00", "close": "22:00" },
      { "day": "토", "open": "11:00", "close": "22:00" },
      { "day": "일", "open": "11:00", "close": "21:00" }
    ],
    "imageUrls": [
      "https://place.kakao.com/image1.jpg",
      "https://place.kakao.com/image2.jpg"
    ],
    "kakaoPlaceUrl": "https://place.map.kakao.com/12345678",
    "kakaoRating": 4.2,
    "kakaoReviewCount": 128
  },
  "error": null
}
```

---

#### API Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/regions` | 권역 목록 조회 |
| `POST` | `/courses` | 코스 생성 |
| `GET` | `/courses/{courseId}` | 코스 상세 조회 |
| `POST` | `/courses/{courseId}/regenerate` | 코스 재생성 |
| `POST` | `/courses/{courseId}/ratings` | 만족도 평가 제출 |
| `GET` | `/places/{placeId}` | 장소 상세 조회 |

---

#### Rate Limiting

| Endpoint | Limit | Window |
|----------|-------|--------|
| `POST /courses` | 10회 | 1분 |
| `POST /courses/{id}/regenerate` | 5회 | 1분 |
| 기타 | 100회 | 1분 |

Rate limit 초과 시 `429 Too Many Requests` 응답과 함께 `RATE_LIMIT_EXCEEDED` 에러 반환

#### 기술 선택 근거

| 선택 | 사유 |
|------|------|
| **Next.js** | SSR/SSG 지원으로 SEO 최적화, 추후 앱 전환 시 React Native와 코드 공유 용이 |
| **Kotlin + Spring Boot** | Java 21 기반 안정성, Coroutines로 비동기 처리 효율화, 국내 개발자 풀 풍부 |
| **PostgreSQL** | JSONB 지원으로 코스 데이터 유연하게 저장, 검증된 안정성 |

#### 향후 확장 계획

```
[Phase 1: MVP]
Web (Next.js) → API (Spring Boot) → PostgreSQL

[Phase 2: App 출시]
Web (Next.js) ──┐
                ├→ API (Spring Boot) → PostgreSQL
App (React Native) ┘
```

---

### F1. 데이트 조건 입력

#### User Story

> **"사용자로서, 지역/데이트 유형/예산을 쉽고 빠르게 입력하고 싶다. 복잡한 설정 없이 핵심 조건만으로 추천받기 위해서다."**

#### 기능 설명

- 3단계 조건 입력 플로우
- 각 단계별 선택지 제공
- 입력 내용 요약 확인 후 코스 생성 요청

#### 입력 필드 정의

| 필드 | 타입 | 필수 여부 | 선택지/범위 |
|------|------|-----------|-------------|
| 지역 | Single Select | 필수 | 서울 20개 권역 + 경기 주요 10개 권역 (상세 목록 하단 참조) |
| 데이트 유형 | Single Select | 필수 | 감성/로맨틱, 액티비티, 맛집 탐방, 문화/예술, 힐링 |
| 예산 (1인 기준) | Single Select | 필수 | ~3만원, 3~5만원, 5~10만원, 10만원~ |
| 특별 요청 | Text Input | 선택 | 최대 100자 (예: "사진 찍기 좋은 곳 위주로") |

#### 지역(권역) 목록 정의

**서울 권역 (20개)**

| 권역명 | 포함 지역 | 특징 |
|--------|----------|------|
| 홍대/합정 | 홍대입구, 합정, 상수, 망원 | 인디 감성, 클럽/펍, 젊은 상권 |
| 연남/연희 | 연남동, 연희동 | 카페 골목, 한적한 데이트 |
| 신촌/이대 | 신촌, 이대, 아현 | 대학가, 가성비 맛집 |
| 성수/서울숲 | 성수동, 서울숲 | 힙플레이스, 카페, 갤러리 |
| 건대/성수 | 건대입구, 자양동 | 먹자골목, 쇼핑 |
| 왕십리/한양대 | 왕십리, 한양대 | 대학가, 영화관 |
| 강남역 | 강남역 일대 | 대형 상권, 맛집 밀집 |
| 역삼/선릉 | 역삼, 선릉, 삼성 | 오피스 상권, 고급 레스토랑 |
| 신사/가로수길 | 신사동, 가로수길 | 패션, 브런치 카페 |
| 압구정/청담 | 압구정, 청담동 | 하이엔드, 프리미엄 |
| 잠실/송파 | 잠실, 석촌호수, 롯데월드 | 대형 복합몰, 놀이공원 |
| 여의도 | 여의도 전역 | 한강공원, 금융가 |
| 이태원/한남 | 이태원, 한남동, 경리단길 | 이국적 분위기, 루프탑 |
| 명동/을지로 | 명동, 을지로, 충무로 | 쇼핑, 힙지로 |
| 종로/광화문 | 종로, 광화문, 인사동 | 전통/역사, 고궁 |
| 북촌/삼청 | 북촌, 삼청동, 안국 | 한옥마을, 갤러리 |
| 대학로/혜화 | 혜화, 대학로 | 연극, 뮤지컬, 소극장 |
| 망원/마포 | 망원동, 마포 | 로컬 맛집, 한강 |
| 영등포/타임스퀘어 | 영등포, 신도림 | 대형 쇼핑몰 |
| 노원/수락산 | 노원, 공릉, 수락산 | 자연, 가성비 |

**경기 권역 (10개)**

| 권역명 | 포함 지역 | 특징 |
|--------|----------|------|
| 판교/정자 | 판교, 정자동 | 카페거리, IT밸리 |
| 수원/광교 | 수원역, 광교, 행궁동 | 수원화성, 카페거리 |
| 분당/서현 | 분당, 서현, 야탑 | 정자카페거리, 맛집 |
| 일산/라페스타 | 일산, 라페스타, 웨스턴돔 | 호수공원, 엔터테인먼트 |
| 부천/중동 | 부천역, 중동 | 영화테마파크, 상동 |
| 인천/송도 | 송도, 연수 | 센트럴파크, 현대적 도시 |
| 용인/수지 | 수지, 동백, 죽전 | 신도시, 가족 친화 |
| 고양/화정 | 화정, 행신 | 대형마트, 주거상권 |
| 안양/범계 | 범계, 평촌 | 학원가, 로데오거리 |
| 하남/스타필드 | 하남, 미사 | 대형 복합몰 |

#### Acceptance Criteria

**AC 1.1: 지역(권역) 선택**

```
Given: 사용자가 코스 생성 화면에 진입했을 때
When: "지역 선택" 필드를 탭하면
Then:
  - 지역 선택 바텀시트가 표시된다
  - "서울" / "경기" 두 개의 탭으로 구분된다
  - 서울 탭: 20개 권역이 카드/칩 형태로 표시된다
  - 경기 탭: 10개 권역이 카드/칩 형태로 표시된다
  - 각 권역에는 대표 키워드가 함께 표시된다 (예: "홍대/합정 - 인디감성, 클럽")
  - 검색 필드를 통해 권역명 또는 포함 지역명으로 필터링이 가능하다
  - 하나의 권역만 선택 가능하다
```

**AC 1.1-1: 권역 검색 기능**

```
Given: 지역 선택 바텀시트가 열린 상태에서
When: 검색 필드에 "성수"를 입력하면
Then:
  - "성수/서울숲" 권역과 "건대/성수" 권역이 필터링되어 표시된다
  - 검색어와 매칭되는 부분이 하이라이트 처리된다
  - 검색 결과가 없으면 "검색 결과가 없습니다" 메시지가 표시된다
```

**AC 1.2: 데이트 유형 선택**

```
Given: 사용자가 지역을 선택 완료했을 때
When: "데이트 유형" 필드를 탭하면
Then:
  - 5개 유형이 아이콘과 함께 카드 형태로 표시된다
  - 각 유형에 간단한 설명이 포함된다 (예: "감성/로맨틱 - 분위기 좋은 카페와 야경")
  - 하나의 유형만 선택 가능하다
```

**AC 1.3: 예산 선택**

```
Given: 사용자가 데이트 유형을 선택 완료했을 때
When: "예산" 필드를 탭하면
Then:
  - 4개 예산 범위가 버튼 형태로 표시된다
  - "1인 기준" 안내 문구가 함께 표시된다
  - 예산 범위별 예상 활동이 힌트로 제공된다 (예: "3~5만원: 카페 + 맛집 가능")
```

**AC 1.4: 조건 요약 및 코스 생성 요청**

```
Given: 사용자가 필수 3개 조건을 모두 선택했을 때
When: "코스 추천받기" 버튼을 탭하면
Then:
  - 선택한 조건이 요약되어 확인 화면에 표시된다
  - "이대로 추천받기" 버튼이 활성화된다
  - 버튼 탭 시 로딩 인디케이터와 함께 AI 코스 생성이 시작된다
```

**AC 1.5: 필수 조건 미입력 방지**

```
Given: 사용자가 조건 입력 화면에 있을 때
When: 필수 조건(지역/유형/예산) 중 하나라도 선택하지 않은 상태에서 "코스 추천받기" 버튼을 탭하면
Then:
  - 버튼이 비활성화 상태로 탭이 불가능하거나
  - 미입력 필드로 스크롤되며 "필수 항목입니다" 안내가 표시된다
```

---

### F2. AI 맞춤형 코스 추천

#### User Story

> **"사용자로서, 내가 입력한 조건에 딱 맞는 데이트 코스를 AI가 추천해주길 원한다. 직접 검색하고 조합하는 수고를 덜기 위해서다."**

#### 기능 설명

- 입력 조건 기반 AI 코스 생성
- 3개 장소로 구성된 시간대별 코스 (카페 → 액티비티/메인 → 식사)
- 장소 간 이동 동선 최적화
- 카카오 Local API 기반 실제 장소 매칭

#### 코스 구조

```
[추천 코스 구조]

1️⃣ 첫 번째 장소 (14:00~15:30)
   - 유형: 카페/디저트
   - 예상 체류: 1~1.5시간
   - 예상 비용: 1인 1~2만원

2️⃣ 두 번째 장소 (16:00~18:00)
   - 유형: 메인 액티비티 (전시/체험/쇼핑 등)
   - 예상 체류: 1.5~2시간
   - 예상 비용: 유형별 상이

3️⃣ 세 번째 장소 (18:30~20:00)
   - 유형: 저녁 식사
   - 예상 체류: 1~1.5시간
   - 예상 비용: 1인 2~4만원
```

#### Acceptance Criteria

**AC 2.1: 코스 생성 성공**

```
Given: 사용자가 유효한 조건(지역/유형/예산)을 입력하고 코스 생성을 요청했을 때
When: AI 코스 생성이 완료되면
Then:
  - 3개 장소로 구성된 코스가 카드 형태로 표시된다
  - 각 장소에는 다음 정보가 포함된다: 장소명, 카테고리, 주소, 예상 비용, 추천 이유
  - 장소 순서가 시간대별로 정렬되어 있다 (오후 → 저녁)
  - 총 예상 비용(1인)이 입력한 예산 범위 이내이다
```

**AC 2.2: 코스 생성 시간 제한**

```
Given: 사용자가 코스 생성을 요청했을 때
When: 로딩이 시작되면
Then:
  - 로딩 인디케이터와 함께 "AI가 최적의 코스를 찾고 있어요" 메시지가 표시된다
  - 코스 생성이 30초 이내에 완료된다
  - 30초 초과 시 타임아웃 에러 메시지와 "다시 시도" 버튼이 표시된다
```

**AC 2.3: 코스 재생성 기능**

```
Given: 사용자가 추천된 코스를 확인했을 때
When: "다른 코스 보기" 버튼을 탭하면
Then:
  - 동일 조건으로 새로운 코스가 생성된다
  - 이전 코스와 최소 1개 이상 다른 장소가 포함된다
  - 재생성 횟수가 화면에 표시된다 (예: "2번째 추천")
```

**AC 2.4: 장소 데이터 유효성**

```
Given: AI가 코스를 생성했을 때
When: 각 장소 정보를 카카오 Local API로 검증하면
Then:
  - 모든 장소가 카카오맵에서 유효한 POI로 확인된다
  - 영업 중인 장소만 포함된다 (폐업 장소 제외)
  - 장소 간 이동 거리가 각각 30분 이내이다 (도보 또는 대중교통 기준)
```

**AC 2.5: 코스 생성 실패 처리**

```
Given: 코스 생성 중 오류가 발생했을 때
When: API 에러 또는 조건에 맞는 장소가 없는 경우
Then:
  - 사용자 친화적 에러 메시지가 표시된다
    - API 에러: "일시적인 오류가 발생했어요. 다시 시도해주세요"
    - 장소 부족: "조건에 맞는 장소가 부족해요. 지역이나 예산을 조정해보세요"
  - "다시 시도" 또는 "조건 수정" 버튼이 제공된다
```

---

### F3. 코스 상세 보기 및 장소 정보

#### User Story

> **"사용자로서, 추천된 각 장소의 상세 정보를 확인하고 싶다. 실제로 방문하기 전에 장소를 파악하고 판단하기 위해서다."**

#### 기능 설명

- 코스 전체 지도 뷰
- 개별 장소 상세 정보 카드
- 카카오맵 연동 (길찾기)

#### Acceptance Criteria

**AC 3.1: 코스 지도 표시**

```
Given: 코스 추천 결과 화면에서
When: 지도 영역을 확인하면
Then:
  - 카카오맵에 3개 장소가 마커로 표시된다
  - 마커에 순서 번호(1, 2, 3)가 표시된다
  - 장소 간 이동 경로가 점선으로 연결된다
  - 지도 줌/이동이 가능하다
```

**AC 3.2: 장소 상세 정보 확인**

```
Given: 코스 결과 화면에서 특정 장소 카드가 표시될 때
When: 장소 카드를 탭하면
Then:
  - 장소 상세 바텀시트가 표시된다
  - 다음 정보가 포함된다:
    - 장소명, 카테고리
    - 주소 (탭 시 클립보드 복사)
    - 영업시간
    - 전화번호 (탭 시 전화 앱 연결)
    - 카카오맵 평점 (있는 경우)
    - AI 추천 이유 (1~2문장)
```

**AC 3.3: 카카오맵 연동**

```
Given: 장소 상세 바텀시트가 열려있을 때
When: "카카오맵에서 보기" 버튼을 탭하면
Then:
  - 카카오맵 앱이 설치된 경우: 해당 장소 상세 화면으로 이동한다
  - 미설치된 경우: 카카오맵 웹 페이지가 브라우저에서 열린다
```

**AC 3.4: 장소 간 길찾기**

```
Given: 코스 결과 화면에서
When: 장소 A와 장소 B 사이의 "이동 경로" 버튼을 탭하면
Then:
  - 카카오맵 길찾기가 실행된다 (출발지: A, 도착지: B)
  - 예상 이동 시간이 사전에 표시된다 (예: "도보 15분")
```

---

### F4. 코스 저장 (로컬 스토리지)

#### User Story

> **"사용자로서, 마음에 드는 코스를 저장하고 나중에 다시 보고 싶다. 로그인 없이도 내 브라우저에서 저장한 코스를 확인하기 위해서다."**

#### 기능 설명

- 브라우저 로컬 스토리지 기반 코스 저장
- 저장된 코스 목록 조회
- 저장된 코스 삭제
- 최대 20개 코스 저장 (초과 시 가장 오래된 코스 자동 삭제 안내)

#### Acceptance Criteria

**AC 4.1: 코스 저장**

```
Given: 코스 추천 결과 화면에서
When: "이 코스 저장하기" 버튼(하트 아이콘)을 탭하면
Then:
  - 버튼이 채워진 하트로 변경된다
  - "코스가 저장되었습니다" 토스트 메시지가 표시된다
  - 해당 코스가 브라우저 로컬 스토리지에 저장된다
  - 저장 시점의 코스 정보가 스냅샷으로 저장된다
```

**AC 4.2: 내 코스 목록 조회**

```
Given: 사용자가 하단 네비게이션에서 "내 코스" 탭을 탭했을 때
When: 내 코스 화면이 로드되면
Then:
  - 로컬 스토리지에 저장된 코스 목록이 최신순으로 표시된다
  - 각 코스 카드에는 지역, 유형, 저장 날짜가 표시된다
  - 저장된 코스가 없으면 "아직 저장한 코스가 없어요" 빈 상태가 표시된다
  - 코스 카드 탭 시 해당 코스 상세로 이동한다
```

**AC 4.3: 저장된 코스 삭제**

```
Given: 내 코스 목록에서 특정 코스를 길게 누르거나, 상세 화면에서 저장 버튼을 다시 탭했을 때
When: 저장 해제를 시도하면
Then:
  - "저장을 취소할까요?" 확인 다이얼로그가 표시된다
  - 확인 시 코스가 로컬 스토리지에서 제거된다
  - "저장이 취소되었습니다" 토스트 메시지가 표시된다
```

**AC 4.4: 저장 용량 제한 안내**

```
Given: 사용자가 이미 20개의 코스를 저장한 상태에서
When: 새로운 코스를 저장하려고 하면
Then:
  - "저장 공간이 가득 찼습니다. 기존 코스를 삭제해주세요" 안내가 표시된다
  - 내 코스 목록으로 이동할 수 있는 버튼이 제공된다
```

**AC 4.5: 브라우저 데이터 안내**

```
Given: 사용자가 내 코스 화면에 처음 진입했을 때
When: 화면이 로드되면
Then:
  - 하단에 "저장된 코스는 이 브라우저에서만 확인할 수 있어요" 안내 문구가 표시된다
  - "로그인하면 어디서든 내 코스를 볼 수 있어요 (준비중)" 문구로 향후 기능 예고
```

---

### F5. 코스 만족도 평가

#### User Story

> **"사용자로서, 추천받은 코스에 대한 피드백을 남기고 싶다. 서비스 개선에 기여하고 더 나은 추천을 받기 위해서다."**

#### 기능 설명

- 코스 생성 직후 간편 평가
- 데이트 후 상세 평가 (푸시 알림 유도)

#### Acceptance Criteria

**AC 5.1: 즉시 만족도 평가**

```
Given: 코스 추천 결과가 표시된 직후
When: 화면 하단의 "이 추천이 마음에 드시나요?" 섹션이 표시되면
Then:
  - 5개 별점 또는 5개 이모지(😞😐🙂😊🥰) 선택 UI가 표시된다
  - 한 번 탭으로 평가가 완료된다
  - 평가 완료 시 "소중한 의견 감사합니다!" 메시지가 표시된다
  - 평가는 1회만 가능하며, 이후 UI가 평가 완료 상태로 변경된다
```

**AC 5.2: 평가 스킵 가능**

```
Given: 만족도 평가 UI가 표시되었을 때
When: 사용자가 평가하지 않고 다른 액션을 취하면
Then:
  - 평가 없이 진행 가능하다 (강제 평가 없음)
  - 24시간 후 푸시 알림으로 평가를 재요청할 수 있다 (추후 구현)
```

**AC 5.3: 평가 데이터 수집**

```
Given: 사용자가 만족도 평가를 제출했을 때
When: 평가 데이터가 서버로 전송되면
Then:
  - 다음 정보가 저장된다:
    - 익명 세션 ID, 코스 ID, 평점(1-5), 평가 시점, 생성 조건(지역/유형/예산)
  - 개인 식별 정보 없이 익명 통계로만 활용된다
```

---

## 5. 범위 외 (Out of Scope)

### 5.1 이번 MVP에서 만들지 않는 기능

| 기능 | 제외 사유 | 향후 계획 |
|------|----------|----------|
| **로그인/회원가입** | MVP는 진입 장벽 최소화, 비로그인으로 핵심 기능 검증 | v1.1에서 카카오 로그인 도입 (개인화 추천, 클라우드 동기화 시 필요) |
| **예약 연동** | 외부 예약 시스템 연동 복잡도 높음 | v1.2에서 네이버/카카오 예약 연동 검토 |
| **실시간 웨이팅 정보** | 데이터 수집 파이프라인 필요 | v1.2에서 제휴 통한 데이터 확보 후 |
| **유저 간 코스 공유** | 소셜 기능은 PMF 검증 후 | v1.1에서 카카오톡 공유 기능 추가 |
| **코스 커스터마이징** | MVP는 AI 추천 검증에 집중 | v1.1에서 장소 교체 기능 추가 |
| **리뷰/평점 작성** | UGC 관리 리소스 필요 | v1.2에서 검토 |
| **다국어 지원** | 국내 타겟 우선 | 해외 확장 시 검토 |
| **결제/프리미엄 기능** | 무료 MVP로 시장 검증 우선 | PMF 달성 후 모델 설계 |

### 5.2 기술적 제약으로 인한 제외

| 항목 | 설명 |
|------|------|
| **오프라인 모드** | MVP는 온라인 필수, 추후 캐싱 전략 수립 |
| **실시간 장소 혼잡도** | 데이터 소스 부재로 제외 |
| **AR 네비게이션** | 기술적 복잡도 높음 |

### 5.3 지역 범위 제한

**MVP 지원 지역 (Phase 1)**
- 서울: 20개 권역 (홍대/합정, 성수/서울숲, 강남역, 잠실/송파 등)
- 경기: 10개 권역 (판교/정자, 수원/광교, 분당/서현, 일산/라페스타 등)
- 상세 권역 목록은 "F2. 데이트 조건 입력" 섹션 참조

**미지원 지역**
- 서울/경기 외 지방 도시 → v1.1 이후 주요 광역시(부산, 대구, 대전) 순차 확대

---

## 6. 릴리즈 계획

### 6.1 MVP 마일스톤

| 단계 | 기간 | 주요 활동 |
|------|------|----------|
| **Design** | 2주 | 와이어프레임, UI 디자인, 프로토타입 |
| **Development** | 4주 | 프론트엔드, 백엔드, AI 연동 |
| **QA & Testing** | 1주 | 기능 테스트, 사용성 테스트 |
| **Soft Launch** | 1주 | 내부 베타, 버그 수정 |
| **Public Launch** | - | 앱스토어 출시 |

### 6.2 성공 기준 달성 시 Next Step

1. **v1.1**: 카카오 로그인, 클라우드 코스 동기화, 카카오톡 공유
2. **v1.2**: 개인화 추천 (로그인 유저 대상), 예약 연동, 리뷰 시스템
3. **v2.0**: 개인화 추천 고도화, 지역 확대

> **로그인 도입 시점**: MVP 성공 지표 달성 후 v1.1에서 선택적 로그인 도입. 메인 기능은 계속 비로그인 유지.

---

## 7. 부록

### 7.1 용어 정의

| 용어 | 정의 |
|------|------|
| 코스 | 3개 장소로 구성된 시간대별 데이트 일정 |
| POI | Point of Interest, 카카오맵 상의 장소 |
| 콜드스타트 | 신규 유저의 행동 데이터 부재 상황 |
| PMF | Product-Market Fit, 제품-시장 적합성 |

### 7.2 참고 문서

- 페르소나 정의서 (별첨 A)
- HEART 지표 상세 설계 (별첨 B)
- 기술 리스크 분석서 (별첨 C)

---

## 8. 인프라 및 배포 티켓

### 8.1 Terraform AWS 인프라 구축

#### 티켓 #1: Terraform 초기 설정 및 프로젝트 구조 생성

**우선순위**: High
**예상 공수**: 3 Story Points

**목표**
- Terraform 프로젝트 구조를 생성하고 AWS Provider 설정을 완료합니다
- 환경별(dev, prod) 설정 분리 및 State 관리 기반을 마련합니다

**작업 내용**
1. Terraform 디렉토리 구조 생성
   ```
   terraform/
   ├── main.tf              # 메인 설정
   ├── variables.tf         # 변수 정의
   ├── outputs.tf           # 출력 값
   ├── backend.tf           # State 백엔드 설정
   ├── versions.tf          # Provider 버전 관리
   ├── modules/             # 모듈 디렉토리
   └── environments/        # 환경별 설정
       ├── dev/
       │   └── terraform.tfvars
       └── prod/
           └── terraform.tfvars
   ```

2. AWS Provider 설정
   - 리전: `ap-northeast-2` (서울)
   - Terraform 버전: `>= 1.0`
   - AWS Provider 버전: `>= 5.0`

3. Backend 설정 (S3 + DynamoDB)
   - S3 버킷: `lian-date-terraform-state`
   - DynamoDB 테이블: `lian-date-terraform-locks`
   - State 파일 암호화 활성화

4. 환경별 변수 정의
   - `environment`: dev, prod
   - `project_name`: lian-date-app
   - `region`: ap-northeast-2

**Acceptance Criteria**
- `terraform init` 명령어가 정상 실행됨
- Backend 설정이 완료되어 State가 S3에 저장됨
- 환경별 tfvars 파일로 환경 분리가 가능함

---

#### 티켓 #2: VPC 및 네트워크 리소스 구성

**우선순위**: High
**예상 공수**: 5 Story Points
**의존성**: 티켓 #1

**목표**
- AWS VPC 및 네트워크 인프라를 Terraform으로 구성합니다
- Multi-AZ 구성으로 고가용성을 확보합니다

**작업 내용**
1. VPC 생성
   - CIDR 블록: `10.0.0.0/16`
   - DNS hostname 활성화
   - DNS resolution 활성화

2. Subnet 생성 (Multi-AZ)
   - Public Subnet
     - `10.0.1.0/24` (ap-northeast-2a)
     - `10.0.2.0/24` (ap-northeast-2c)
   - Private Subnet
     - `10.0.11.0/24` (ap-northeast-2a)
     - `10.0.12.0/24` (ap-northeast-2c)

3. Internet Gateway & NAT Gateway
   - Internet Gateway: Public Subnet 연결
   - NAT Gateway: Public Subnet에 배치 (Elastic IP 할당)

4. Route Tables
   - Public Route Table: Internet Gateway 라우팅
   - Private Route Table: NAT Gateway 라우팅

5. VPC Endpoints (비용 절감)
   - S3 Gateway Endpoint
   - ECR API Endpoint
   - ECR DKR Endpoint

**산출물**
- `terraform/modules/network/vpc.tf`
- `terraform/modules/network/subnets.tf`
- `terraform/modules/network/gateways.tf`
- `terraform/modules/network/routes.tf`
- `terraform/modules/network/endpoints.tf`
- `terraform/modules/network/outputs.tf`

**Acceptance Criteria**
- VPC가 정상 생성되고 2개 AZ에 걸쳐 구성됨
- Public Subnet에서 인터넷 접근 가능
- Private Subnet에서 NAT Gateway를 통한 아웃바운드 연결 가능
- VPC Endpoint를 통해 S3, ECR 접근 가능

---

#### 티켓 #3: Security Groups 구성

**우선순위**: High
**예상 공수**: 3 Story Points
**의존성**: 티켓 #2

**목표**
- 각 리소스별 보안 그룹을 정의하여 최소 권한 원칙을 적용합니다

**작업 내용**
1. ALB Security Group
   - Inbound: 80 (HTTP), 443 (HTTPS) from 0.0.0.0/0
   - Outbound: All traffic

2. ECS Security Group
   - Inbound: 8080 (App Port) from ALB Security Group
   - Outbound: All traffic (ECR, RDS, External API 접근 필요)

3. RDS Security Group
   - Inbound: 5432 (PostgreSQL) from ECS Security Group
   - Outbound: None

**산출물**
- `terraform/modules/security/security_groups.tf`
- `terraform/modules/security/outputs.tf`

**Acceptance Criteria**
- 각 Security Group이 최소 권한으로 구성됨
- ALB → ECS → RDS 연결이 정상 작동함
- 불필요한 포트가 열려있지 않음

---

#### 티켓 #4: ECR Repository 생성

**우선순위**: High
**예상 공수**: 2 Story Points
**의존성**: 티켓 #1

**목표**
- Docker 이미지를 저장할 ECR Repository를 생성합니다

**작업 내용**
1. ECR Repository 생성
   - Repository 이름: `lian-date-app-backend`
   - Image tag mutability: `MUTABLE`
   - Image scanning: 활성화 (보안 취약점 스캔)

2. Lifecycle Policy 설정
   - 최근 10개 이미지만 유지
   - Untagged 이미지는 1일 후 삭제

3. Repository Policy 설정
   - ECS Task Execution Role에 Pull 권한 부여

**산출물**
- `terraform/modules/ecr/repository.tf`
- `terraform/modules/ecr/lifecycle_policy.tf`
- `terraform/modules/ecr/outputs.tf`

**Acceptance Criteria**
- ECR Repository가 생성됨
- Lifecycle Policy가 적용되어 오래된 이미지가 자동 삭제됨
- ECS에서 이미지 Pull 가능

---

#### 티켓 #5: RDS PostgreSQL 구성

**우선순위**: High
**예상 공수**: 5 Story Points
**의존성**: 티켓 #2, #3

**목표**
- PostgreSQL RDS 인스턴스를 생성하고 백업/보안 설정을 완료합니다

**작업 내용**
1. DB Subnet Group 생성
   - Private Subnet 2개 지정 (Multi-AZ)

2. RDS Instance 생성
   - Engine: PostgreSQL 16
   - Instance Class
     - Dev: `db.t3.micro`
     - Prod: `db.t3.small` (추후 확장)
   - Storage: 20GB (General Purpose SSD)
   - Multi-AZ: Prod 환경만 활성화
   - Backup retention: 7일

3. Parameter Group 설정
   - `max_connections`: 100
   - `shared_buffers`: 적절한 값 설정
   - Timezone: `Asia/Seoul`

4. Secrets Manager 연동
   - DB 자격증명을 Secrets Manager에 저장
   - 자동 로테이션 설정 (30일)

**산출물**
- `terraform/modules/rds/db_subnet_group.tf`
- `terraform/modules/rds/db_instance.tf`
- `terraform/modules/rds/parameter_group.tf`
- `terraform/modules/rds/secrets.tf`
- `terraform/modules/rds/outputs.tf`

**Acceptance Criteria**
- RDS 인스턴스가 Private Subnet에 생성됨
- ECS에서 RDS 연결 가능
- 자동 백업이 7일간 유지됨
- DB 자격증명이 Secrets Manager에 안전하게 저장됨

---

#### 티켓 #6: Application Load Balancer (ALB) 구성

**우선순위**: High
**예상 공수**: 4 Story Points
**의존성**: 티켓 #2, #3

**목표**
- Public Subnet에 ALB를 배치하여 트래픽을 ECS로 라우팅합니다

**작업 내용**
1. ALB 생성
   - Type: Application Load Balancer
   - Scheme: Internet-facing
   - Subnets: Public Subnet 2개 (Multi-AZ)
   - Security Group: ALB Security Group

2. Target Group 생성
   - Target Type: IP (Fargate 사용)
   - Protocol: HTTP
   - Port: 8080
   - Health Check
     - Path: `/actuator/health`
     - Interval: 30초
     - Timeout: 5초
     - Healthy threshold: 2
     - Unhealthy threshold: 3

3. Listener 설정
   - HTTP (80): HTTPS로 리다이렉트
   - HTTPS (443): Target Group으로 포워딩
   - SSL Certificate: ACM 인증서 연결 (추후 설정)

**산출물**
- `terraform/modules/alb/alb.tf`
- `terraform/modules/alb/target_group.tf`
- `terraform/modules/alb/listener.tf`
- `terraform/modules/alb/outputs.tf`

**Acceptance Criteria**
- ALB가 Public Subnet에 생성됨
- Health Check가 정상 작동함
- HTTP → HTTPS 리다이렉트 작동
- Target Group에 ECS Task 등록 가능

---

#### 티켓 #7: ECS Cluster 및 Fargate Service 구성

**우선순위**: High
**예상 공수**: 8 Story Points
**의존성**: 티켓 #2, #3, #4, #6

**목표**
- ECS Cluster와 Fargate Service를 구성하여 컨테이너 애플리케이션을 배포합니다

**작업 내용**
1. ECS Cluster 생성
   - Cluster 이름: `lian-date-cluster`
   - Capacity provider: FARGATE, FARGATE_SPOT

2. Task Definition 작성
   - Launch Type: FARGATE
   - CPU: 512 (.5 vCPU)
   - Memory: 1024 MB (1 GB)
   - Container Definition
     - Image: ECR Repository URI
     - Port: 8080
     - Environment Variables
       - `SPRING_PROFILES_ACTIVE`: dev/prod
       - `KAKAO_API_KEY`: Secrets Manager 참조
       - `OPENAI_API_KEY`: Secrets Manager 참조
       - `DB_HOST`, `DB_PORT`, `DB_NAME`: RDS 정보
       - `DB_USERNAME`, `DB_PASSWORD`: Secrets Manager 참조
   - Logging: CloudWatch Logs

3. IAM Role 생성
   - Task Execution Role
     - ECR Pull 권한
     - CloudWatch Logs 쓰기 권한
     - Secrets Manager 읽기 권한
   - Task Role
     - S3 접근 권한 (추후 파일 업로드용)

4. ECS Service 생성
   - Desired Count: 2 (Dev: 1, Prod: 2)
   - Deployment Configuration
     - Rolling Update
     - Minimum healthy percent: 100
     - Maximum percent: 200
   - Network Configuration
     - Subnets: Private Subnet
     - Security Group: ECS Security Group
   - Load Balancer
     - ALB Target Group 연결
   - Auto Scaling (선택사항)
     - Target CPU: 70%
     - Min: 1, Max: 4

**산출물**
- `terraform/modules/ecs/cluster.tf`
- `terraform/modules/ecs/task_definition.tf`
- `terraform/modules/ecs/iam_roles.tf`
- `terraform/modules/ecs/service.tf`
- `terraform/modules/ecs/autoscaling.tf`
- `terraform/modules/ecs/cloudwatch.tf`
- `terraform/modules/ecs/outputs.tf`

**Acceptance Criteria**
- ECS Cluster가 생성됨
- Task Definition이 정상 등록됨
- ECS Service가 Private Subnet에서 실행됨
- ALB를 통해 애플리케이션 접근 가능
- CloudWatch Logs에 로그가 정상 기록됨
- Auto Scaling이 CPU 기반으로 작동함

---

#### 티켓 #8: CloudWatch Monitoring 및 Alarms 설정

**우선순위**: Medium
**예상 공수**: 3 Story Points
**의존성**: 티켓 #7

**목표**
- CloudWatch를 통한 모니터링 및 알람 설정으로 시스템 안정성을 확보합니다

**작업 내용**
1. CloudWatch Log Groups 생성
   - ECS Task 로그: `/ecs/lian-date-app`
   - ALB 접근 로그: `/aws/alb/lian-date-alb`
   - Retention: 7일 (Dev), 30일 (Prod)

2. CloudWatch Alarms 생성
   - ECS Service
     - CPU Utilization > 80%
     - Memory Utilization > 80%
     - Running Task Count < Desired Count
   - ALB
     - Target Unhealthy Count > 0
     - HTTP 5xx Error Rate > 5%
   - RDS
     - CPU Utilization > 80%
     - Free Storage < 20%
     - Database Connections > 80

3. SNS Topic 생성
   - 알람 발생 시 이메일 알림
   - 추후 Slack 연동 준비

**산출물**
- `terraform/modules/monitoring/log_groups.tf`
- `terraform/modules/monitoring/alarms.tf`
- `terraform/modules/monitoring/sns.tf`
- `terraform/modules/monitoring/outputs.tf`

**Acceptance Criteria**
- CloudWatch Logs에 애플리케이션 로그가 기록됨
- 설정한 임계값 초과 시 알람 발생
- SNS를 통해 이메일 알림 수신

---

### 8.2 GitHub Actions CI/CD 파이프라인

#### 티켓 #9: GitHub Actions 워크플로우 초기 설정

**우선순위**: High
**예상 공수**: 3 Story Points
**의존성**: 티켓 #4, #7

**목표**
- GitHub Actions를 이용한 CI/CD 파이프라인의 기본 구조를 설정합니다

**작업 내용**
1. 디렉토리 구조 생성
   ```
   .github/
   └── workflows/
       ├── ci.yml           # PR 시 테스트/빌드
       ├── cd-dev.yml       # dev 환경 배포
       └── cd-prod.yml      # prod 환경 배포
   ```

2. GitHub Secrets 설정
   - `AWS_ACCESS_KEY_ID`
   - `AWS_SECRET_ACCESS_KEY`
   - `AWS_REGION`
   - `ECR_REPOSITORY`
   - `ECS_CLUSTER`
   - `ECS_SERVICE`
   - `ECS_TASK_DEFINITION`

3. 재사용 가능한 Composite Actions 생성
   - `build-and-test`: Gradle 빌드 및 테스트
   - `docker-build-push`: Docker 이미지 빌드 및 ECR Push

**산출물**
- `.github/workflows/ci.yml`
- `.github/workflows/cd-dev.yml`
- `.github/workflows/cd-prod.yml`
- `.github/actions/build-and-test/action.yml`
- `.github/actions/docker-build-push/action.yml`

**Acceptance Criteria**
- GitHub Secrets가 모두 등록됨
- 워크플로우 파일이 정상적으로 파싱됨
- Composite Actions가 재사용 가능하게 구성됨

---

#### 티켓 #10: CI 파이프라인 구현 (테스트 및 빌드)

**우선순위**: High
**예상 공수**: 5 Story Points
**의존성**: 티켓 #9

**목표**
- Pull Request 생성 시 자동으로 테스트 및 빌드를 수행합니다

**작업 내용**
1. Trigger 설정
   - Event: `pull_request` (branches: `main`, `develop`)
   - Paths: `backend/**`, `.github/workflows/**`

2. Jobs 구성
   - **Job 1: Test**
     - Checkout 코드
     - Java 21 설정
     - Gradle 캐시 설정
     - 단위 테스트 실행 (`./gradlew test`)
     - 통합 테스트 실행 (`./gradlew integrationTest`)
     - 테스트 커버리지 리포트 생성 (JaCoCo)
     - 커버리지 임계값 검증 (80% 이상)

   - **Job 2: Build**
     - Checkout 코드
     - Java 21 설정
     - Gradle 빌드 (`./gradlew bootJar`)
     - Docker 이미지 빌드 (태그: PR 번호)
     - Trivy 보안 스캔 실행

3. Status Check 설정
   - PR 머지 전 CI 통과 필수
   - 테스트 실패 시 PR 블록

**산출물**
- `.github/workflows/ci.yml` (완성)
- `.github/actions/build-and-test/action.yml` (완성)

**Acceptance Criteria**
- PR 생성 시 CI 워크플로우 자동 실행
- 테스트 실패 시 워크플로우 실패
- 빌드 성공 시 Docker 이미지 생성
- 보안 스캔 결과가 PR 코멘트로 표시됨

---

#### 티켓 #11: CD 파이프라인 구현 - Dev 환경

**우선순위**: High
**예상 공수**: 5 Story Points
**의존성**: 티켓 #9, #10

**목표**
- develop 브랜치에 머지 시 자동으로 Dev 환경에 배포합니다

**작업 내용**
1. Trigger 설정
   - Event: `push` (branch: `develop`)
   - Paths: `backend/**`, `.github/workflows/**`

2. Jobs 구성
   - **Job 1: Build and Push**
     - Checkout 코드
     - AWS 자격증명 설정
     - ECR 로그인
     - Docker 이미지 빌드
       - 태그: `dev-{commit_sha}`, `dev-latest`
     - ECR에 이미지 Push

   - **Job 2: Deploy to ECS**
     - 새로운 Task Definition 등록
       - 이미지 URI: ECR에서 Push한 이미지
       - Environment: `dev`
     - ECS Service 업데이트
       - Force new deployment
       - Wait for stability (타임아웃: 10분)
     - Deployment 결과 슬랙 알림 (선택사항)

3. Rollback 전략
   - 배포 실패 시 이전 Task Definition으로 자동 롤백
   - Deployment Circuit Breaker 활성화

**산출물**
- `.github/workflows/cd-dev.yml` (완성)
- `.github/actions/docker-build-push/action.yml` (완성)

**Acceptance Criteria**
- develop 브랜치 머지 시 자동 배포
- ECR에 이미지가 정상 Push됨
- ECS Service가 새로운 Task Definition으로 업데이트됨
- Health Check 통과 후 배포 완료
- 배포 실패 시 자동 롤백

---

#### 티켓 #12: CD 파이프라인 구현 - Prod 환경

**우선순위**: High
**예상 공수**: 5 Story Points
**의존성**: 티켓 #11

**목표**
- main 브랜치에 머지 시 수동 승인 후 Prod 환경에 배포합니다

**작업 내용**
1. Trigger 설정
   - Event: `push` (branch: `main`)
   - Paths: `backend/**`, `.github/workflows/**`

2. Jobs 구성
   - **Job 1: Build and Push**
     - Checkout 코드
     - Git 태그 생성 (SemVer: v1.0.0)
     - AWS 자격증명 설정
     - ECR 로그인
     - Docker 이미지 빌드
       - 태그: `prod-{version}`, `prod-latest`, `{version}`
     - ECR에 이미지 Push

   - **Job 2: Manual Approval** (GitHub Environment 사용)
     - Environment: `production`
     - Required reviewers: 2명
     - 승인 대기 (타임아웃: 24시간)

   - **Job 3: Deploy to ECS**
     - 새로운 Task Definition 등록
       - 이미지 URI: ECR에서 Push한 이미지
       - Environment: `prod`
     - Blue/Green Deployment (선택사항)
       - CodeDeploy 연동
       - Traffic shifting: Linear10PercentEvery1Minute
     - ECS Service 업데이트
       - Wait for stability (타임아웃: 15분)
     - Deployment 결과 슬랙 알림

3. Release Notes 자동 생성
   - GitHub Release 생성
   - CHANGELOG.md 업데이트
   - Jira 티켓 자동 업데이트 (선택사항)

**산출물**
- `.github/workflows/cd-prod.yml` (완성)
- `scripts/generate-release-notes.sh`

**Acceptance Criteria**
- main 브랜치 머지 시 빌드 및 ECR Push
- Manual Approval 단계에서 대기
- 승인 후 Prod 환경에 배포
- GitHub Release 자동 생성
- 배포 완료 후 슬랙 알림

---

#### 티켓 #13: 배포 모니터링 및 알림 설정

**우선순위**: Medium
**예상 공수**: 3 Story Points
**의존성**: 티켓 #11, #12

**목표**
- 배포 상태를 실시간으로 모니터링하고 알림을 받습니다

**작업 내용**
1. GitHub Actions Status Badge 추가
   - README.md에 워크플로우 상태 배지 추가
   - CI, CD-Dev, CD-Prod 각각 표시

2. Slack 알림 설정
   - 채널: `#deployments`
   - 알림 내용
     - 배포 시작: 환경, 버전, 커밋 정보
     - 배포 성공: 소요 시간, 배포자, 릴리즈 노트 링크
     - 배포 실패: 에러 메시지, 로그 링크, 롤백 상태
   - Slack Incoming Webhook 사용

3. 배포 히스토리 대시보드
   - CloudWatch Dashboard 생성
   - 배포 빈도, 성공률, 소요 시간 그래프
   - ECS Service Events 로그

**산출물**
- `.github/workflows/notify-slack.yml`
- `README.md` (배지 추가)
- CloudWatch Dashboard JSON 파일

**Acceptance Criteria**
- GitHub Actions 상태 배지가 README에 표시됨
- 배포 이벤트 시 Slack 알림 수신
- CloudWatch Dashboard에서 배포 히스토리 확인 가능

---

#### 티켓 #14: Dockerfile 최적화 및 Multi-stage Build

**우선순위**: Medium
**예상 공수**: 3 Story Points
**의존성**: 티켓 #10

**목표**
- Docker 이미지 크기를 최적화하고 빌드 시간을 단축합니다

**작업 내용**
1. Multi-stage Build 구현
   ```dockerfile
   # Stage 1: Build
   FROM gradle:8.5-jdk21 AS builder
   WORKDIR /app
   COPY build.gradle settings.gradle ./
   COPY src ./src
   RUN gradle bootJar --no-daemon

   # Stage 2: Runtime
   FROM eclipse-temurin:21-jre-alpine
   WORKDIR /app
   COPY --from=builder /app/build/libs/*.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

2. 이미지 최적화
   - Alpine Linux 사용 (경량화)
   - Layer 캐싱 최적화
   - .dockerignore 파일 작성
   - 불필요한 파일 제외

3. Health Check 추가
   ```dockerfile
   HEALTHCHECK --interval=30s --timeout=3s \
     CMD curl -f http://localhost:8080/actuator/health || exit 1
   ```

4. 보안 강화
   - Non-root 사용자로 실행
   - 최소 권한 원칙 적용

**산출물**
- `backend/Dockerfile` (최적화)
- `backend/.dockerignore`

**Acceptance Criteria**
- 이미지 크기가 300MB 이하
- 빌드 시간이 5분 이내
- Health Check가 정상 작동
- 보안 스캔에서 Critical 취약점 없음

---

#### 티켓 #15: 환경별 설정 관리 및 Secrets 통합

**우선순위**: High
**예상 공수**: 4 Story Points
**의존성**: 티켓 #5, #11, #12

**목표**
- 환경별 설정을 안전하게 관리하고 Secrets Manager와 통합합니다

**작업 내용**
1. Spring Boot 프로파일 설정
   - `application.yml`: 공통 설정
   - `application-dev.yml`: Dev 환경
   - `application-prod.yml`: Prod 환경

2. AWS Secrets Manager 연동
   - Spring Cloud AWS Secrets Manager 의존성 추가
   - 런타임 시 Secrets 자동 주입
   - Secrets 항목
     - `/lian-date/dev/db`: DB 자격증명
     - `/lian-date/dev/kakao`: Kakao API Key
     - `/lian-date/dev/openai`: OpenAI API Key
     - `/lian-date/prod/...`: Prod 환경 동일 구조

3. ECS Task Definition에서 Secrets 참조
   ```json
   "secrets": [
     {
       "name": "DB_PASSWORD",
       "valueFrom": "arn:aws:secretsmanager:ap-northeast-2:xxx:secret:/lian-date/dev/db:password::"
     }
   ]
   ```

4. 로컬 개발 환경 설정
   - `.env.example` 파일 제공
   - LocalStack 또는 AWS Secrets Manager 로컬 모드 사용

**산출물**
- `backend/src/main/resources/application-dev.yml`
- `backend/src/main/resources/application-prod.yml`
- `backend/.env.example`
- Terraform Secrets Manager 리소스 추가

**Acceptance Criteria**
- 환경별로 다른 설정이 적용됨
- Secrets Manager에서 민감 정보 자동 주입
- 로컬 개발 시 .env 파일로 설정 가능
- Git에 민감 정보가 커밋되지 않음

---

### 8.3 구현 순서 및 일정

#### Phase 1: 인프라 기반 구축 (Week 1-2)
1. 티켓 #1: Terraform 초기 설정
2. 티켓 #2: VPC 및 네트워크
3. 티켓 #3: Security Groups
4. 티켓 #4: ECR Repository

#### Phase 2: 데이터베이스 및 컴퓨팅 리소스 (Week 2-3)
5. 티켓 #5: RDS PostgreSQL
6. 티켓 #6: Application Load Balancer
7. 티켓 #7: ECS Cluster 및 Fargate Service
8. 티켓 #8: CloudWatch Monitoring

#### Phase 3: CI/CD 파이프라인 구축 (Week 3-4)
9. 티켓 #9: GitHub Actions 초기 설정
10. 티켓 #10: CI 파이프라인
11. 티켓 #11: CD Dev 환경
12. 티켓 #12: CD Prod 환경

#### Phase 4: 최적화 및 운영 (Week 4)
13. 티켓 #13: 배포 모니터링 및 알림
14. 티켓 #14: Dockerfile 최적화
15. 티켓 #15: 환경별 설정 관리

**예상 총 공수**: 61 Story Points (약 4주)

---

### 8.4 인프라 아키텍처 다이어그램

```
┌─────────────────────────────────────────────────────────────────┐
│                         AWS Cloud (ap-northeast-2)              │
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │                         VPC (10.0.0.0/16)                  │ │
│  │                                                             │ │
│  │  ┌─────────────────────┐      ┌─────────────────────┐     │ │
│  │  │  Public Subnet 2a   │      │  Public Subnet 2c   │     │ │
│  │  │   (10.0.1.0/24)     │      │   (10.0.2.0/24)     │     │ │
│  │  │                     │      │                     │     │ │
│  │  │  ┌───────────────┐ │      │  ┌───────────────┐  │     │ │
│  │  │  │      ALB      │ │      │  │      NAT      │  │     │ │
│  │  │  │  (80, 443)    │◄┼──────┼─►│    Gateway    │  │     │ │
│  │  │  └───────┬───────┘ │      │  └───────────────┘  │     │ │
│  │  └──────────┼──────────┘      └─────────────────────┘     │ │
│  │             │                                              │ │
│  │  ┌──────────▼──────────┐      ┌─────────────────────┐     │ │
│  │  │  Private Subnet 2a  │      │  Private Subnet 2c  │     │ │
│  │  │   (10.0.11.0/24)    │      │   (10.0.12.0/24)    │     │ │
│  │  │                     │      │                     │     │ │
│  │  │  ┌───────────────┐ │      │  ┌───────────────┐  │     │ │
│  │  │  │  ECS Fargate  │ │      │  │  ECS Fargate  │  │     │ │
│  │  │  │     Task      │ │      │  │     Task      │  │     │ │
│  │  │  └───────┬───────┘ │      │  └───────┬───────┘  │     │ │
│  │  │          │         │      │          │          │     │ │
│  │  │  ┌───────▼───────┐ │      │  ┌───────▼───────┐  │     │ │
│  │  │  │      RDS      │ │      │  │  RDS Standby  │  │     │ │
│  │  │  │  PostgreSQL   │ │      │  │  (Multi-AZ)   │  │     │ │
│  │  │  └───────────────┘ │      │  └───────────────┘  │     │ │
│  │  └─────────────────────┘      └─────────────────────┘     │ │
│  │                                                             │ │
│  └───────────────────────────────────────────────────────────┘ │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │     ECR      │  │   Secrets    │  │  CloudWatch  │         │
│  │  Repository  │  │   Manager    │  │     Logs     │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
                              ▲
                              │
                              │ GitHub Actions
                              │ (Docker Build & Push)
                              │
                    ┌─────────┴─────────┐
                    │   GitHub Actions  │
                    │     Workflow      │
                    └───────────────────┘
```

---

### 8.5 비용 예측

**Dev 환경 (월간 예상 비용)**

| 리소스 | 사양 | 예상 비용 |
|--------|------|----------|
| ECS Fargate | 1 Task (0.5 vCPU, 1GB) | $15 |
| RDS PostgreSQL | db.t3.micro (20GB) | $25 |
| ALB | 1개 | $20 |
| NAT Gateway | 1개 | $35 |
| ECR Storage | 10GB | $1 |
| CloudWatch Logs | 5GB | $3 |
| **총계** | - | **$99/월** |

**Prod 환경 (월간 예상 비용)**

| 리소스 | 사양 | 예상 비용 |
|--------|------|----------|
| ECS Fargate | 2 Tasks (0.5 vCPU, 1GB) | $30 |
| RDS PostgreSQL | db.t3.small (20GB, Multi-AZ) | $70 |
| ALB | 1개 | $20 |
| NAT Gateway | 1개 | $35 |
| ECR Storage | 20GB | $2 |
| CloudWatch Logs | 10GB | $5 |
| ACM Certificate | 무료 | $0 |
| **총계** | - | **$162/월** |

**총 예상 비용**: Dev + Prod = **$261/월** (~30만원)

---

**문서 끝**

*이 PRD에 대한 질문이나 피드백은 Product Team으로 연락 바랍니다.*
