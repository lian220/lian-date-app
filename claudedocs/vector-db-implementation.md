# Vector DB 장소 메모리 저장 시스템

AI 큐레이션된 장소 정보를 Chroma DB에 저장하여 의미 기반 검색을 가능하게 하는 시스템입니다.

## 아키텍처

```
PlaceMemoryService (Application Layer)
    ↓ 사용
PlaceMemoryPort (Domain Port)
    ↓ 구현
ChromaPlaceMemoryAdapter (Infrastructure)
    ↓ 사용
ChromaDbClient (REST API Client)
    ↓ 통신
Chroma DB (Docker Container)
```

## 주요 컴포넌트

### 1. PlaceWithCuration (Domain Value Object)
장소 기본 정보 + AI 큐레이션 정보를 함께 보관하는 VO

**기능:**
- `toDocument()`: Vector DB 저장용 자연어 문서 변환
- `getSummary()`: 간단한 요약 정보 생성

**문서 포맷 예시:**
```markdown
# 로맨틱 이탈리안 레스토랑

## 기본 정보
- 장소명: 로맨틱 이탈리안 레스토랑
- 카테고리: 음식점
- 주소: 서울시 강남구 테헤란로 123
- 위치: (위도: 37.5665, 경도: 126.9780)

## 데이트 큐레이션
- 데이트 적합도: 9/10점
- 분위기: #로맨틱, #아늑한, #고급스러운
- 1인당 예상 가격: 50,000원
- 추천 시간대: 저녁 7-9시
- 추천 이유: 분위기가 로맨틱하고 음식이 맛있어 데이트하기 좋은 곳입니다.
```

### 2. PlaceMemoryPort (Domain Port)
Vector DB 저장/검색 인터페이스

**메서드:**
- `addToMemory()`: 단일 장소 저장
- `addBatchToMemory()`: 배치 저장 (10개씩)
- `searchSimilar()`: 의미 기반 검색
- `clearMemory()`: 메모리 초기화
- `count()`: 저장된 장소 개수

### 3. ChromaDbClient (Infrastructure)
Chroma DB REST API 클라이언트

**엔드포인트:**
- `POST /api/v1/collections` - 컬렉션 생성/조회
- `POST /api/v1/collections/{name}/add` - 문서 추가
- `POST /api/v1/collections/{name}/query` - 검색
- `DELETE /api/v1/collections/{name}` - 컬렉션 삭제

### 4. ChromaPlaceMemoryAdapter (Infrastructure)
PlaceMemoryPort 구현체

**기능:**
- PlaceWithCuration ↔ Chroma 메타데이터 변환
- 배치 처리 (10개씩 자동 분할)
- 임베딩 자동 생성 (Chroma 내부)

### 5. PlaceMemoryService (Application)
장소 메모리 구축 및 관리 서비스

**워크플로우:**
```
buildPlaceMemory(placeIds) {
  1. collectPlaces() - 카카오 API에서 장소 정보 수집
  2. curatePlaces() - OpenAI로 AI 큐레이션
  3. saveToBatchMemory() - Chroma DB에 배치 저장
}
```

## Docker Compose 설정

```yaml
chroma:
  image: chromadb/chroma:latest
  container_name: dateclick-chroma
  restart: unless-stopped
  profiles:
    - db
  ports:
    - "${CHROMA_PORT:-8000}:8000"
  environment:
    IS_PERSISTENT: "TRUE"
    ALLOW_RESET: "TRUE"
  volumes:
    - ./data/chroma:/chroma/chroma
```

## Application 설정

```yaml
chroma:
  url: ${CHROMA_DB_URL:http://localhost:8000}
  place-collection-name: date_places
  connection-timeout: 5000
  read-timeout: 30000
  batch-size: 10
```

## REST API

### 1. 장소 메모리 구축
```http
POST /api/v1/place-memory/build
Content-Type: application/json

{
  "placeIds": [
    "kakao-place-123",
    "kakao-place-456",
    "kakao-place-789"
  ]
}
```

**Response:**
```json
{
  "savedCount": 3,
  "totalPlaces": 150
}
```

### 2. 의미 기반 검색
```http
GET /api/v1/place-memory/search?query=로맨틱한 레스토랑&limit=5
```

**Response:**
```json
{
  "query": "로맨틱한 레스토랑",
  "count": 5,
  "places": [
    {
      "id": "kakao-place-123",
      "name": "로맨틱 이탈리안",
      "category": "음식점",
      "dateScore": 9,
      "moodTags": ["#로맨틱", "#아늑한"],
      "priceRange": 50000,
      "bestTime": "저녁 7-9시",
      "recommendation": "분위기가 로맨틱하고...",
      "kakaoRating": 4.5
    }
  ]
}
```

### 3. 메모리 통계
```http
GET /api/v1/place-memory/stats
```

### 4. 메모리 초기화 (개발용)
```http
DELETE /api/v1/place-memory/clear
```

## 사용 시나리오

### 시나리오 1: 특정 지역 장소 메모리 구축
```kotlin
// 1. 카카오 API로 강남 데이트 장소 검색 (외부에서 수행)
val placeIds = listOf("place-1", "place-2", ..., "place-100")

// 2. 메모리 구축 (내부적으로 큐레이션 + 저장)
val savedCount = placeMemoryService.buildPlaceMemory(placeIds)

// 결과: 100개 장소가 AI 큐레이션되어 Vector DB에 저장됨
```

### 시나리오 2: 의미 기반 장소 추천
```kotlin
// 사용자 쿼리
val query = "조용하고 분위기 좋은 카페 추천해줘"

// Vector DB에서 유사도 기반 검색
val results = placeMemoryService.searchPlaces(query, limit = 10)

// 결과: 의미적으로 유사한 상위 10개 장소 반환
```

## 성능 고려사항

### 배치 처리
- 10개씩 끊어서 저장 (chromaDbProperties.batchSize)
- 진행 상황 로깅으로 모니터링 가능

### 임베딩 생성
- Chroma DB가 자동으로 임베딩 생성 (default-embedding-function)
- 별도 OpenAI Embeddings API 호출 불필요

### 메타데이터 활용
- 장소 ID, 이름, 카테고리, 점수 등을 메타데이터로 저장
- 검색 필터링 및 빠른 재구성 가능

## 에러 처리

### 장소 정보 수집 실패
```kotlin
// 실패한 장소는 건너뛰고 계속 진행
val places = collectPlaces(placeIds).mapNotNull { ... }
```

### AI 큐레이션 실패
```kotlin
// 큐레이션 실패 시 해당 장소 제외
val curatedPlaces = curatePlaces(places).mapNotNull { ... }
```

### Chroma DB 연결 실패
```kotlin
// ChromaException 발생, 로그 기록
throw ChromaException("Failed to connect to Chroma DB")
```

## 테스트

### 단위 테스트
```bash
cd backend
./gradlew test --tests "PlaceWithCurationTest"
./gradlew test --tests "PlaceMemoryServiceTest"
```

### 통합 테스트 (Chroma DB 필요)
```bash
# Chroma DB 실행
docker compose --profile db up -d chroma

# 테스트 실행
./gradlew test --tests "ChromaPlaceMemoryAdapterTest"
```

## 실행 방법

### 1. Chroma DB 시작
```bash
docker compose --profile db up -d chroma
```

### 2. 백엔드 시작
```bash
docker compose up -d backend
```

### 3. API 테스트
```bash
# 장소 메모리 구축
curl -X POST http://localhost:8080/api/v1/place-memory/build \
  -H "Content-Type: application/json" \
  -d '{"placeIds": ["place-1", "place-2", "place-3"]}'

# 검색 테스트
curl "http://localhost:8080/api/v1/place-memory/search?query=로맨틱한%20레스토랑&limit=5"
```

## 향후 개선사항

1. **임베딩 모델 커스터마이징**
   - OpenAI Embeddings API 직접 사용
   - 한국어 특화 임베딩 모델 적용

2. **메타데이터 필터링**
   - 가격대, 지역, 카테고리별 필터링
   - 복합 조건 검색

3. **캐싱 전략**
   - 자주 검색되는 쿼리 캐싱
   - Redis 연동

4. **배치 작업 스케줄링**
   - 주기적 메모리 업데이트
   - 새로운 장소 자동 큐레이션

## 참고 자료

- [Chroma DB Documentation](https://docs.trychroma.com/)
- [Vector Database 개념](https://www.pinecone.io/learn/vector-database/)
- [Semantic Search 원리](https://www.elastic.co/what-is/semantic-search)
