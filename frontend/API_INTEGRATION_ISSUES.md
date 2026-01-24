# API 통합 이슈 리포트

**날짜**: 2026-01-24
**테스트 환경**: Docker (Backend: 8080, Frontend: 3000)

---

## ⚠️ 발견된 불일치 사항

### 1. API 포트 설정 불일치
**문제**: 프론트엔드 API 클라이언트와 백엔드 실행 포트가 다름

- **프론트엔드 설정** (`src/lib/api.ts:7`):
  ```typescript
  const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8000';
  ```

- **백엔드 실행 포트**: `8080`

**영향**: 로컬 개발 시 API 호출 실패

**해결 방법**:
```typescript
// Option 1: 코드 수정
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

// Option 2: 환경변수 설정 (.env.local)
NEXT_PUBLIC_API_URL=http://localhost:8080
```

---

### 2. API 응답 형식 불일치
**문제**: 백엔드 응답 구조와 프론트엔드 타입 정의가 다름

#### 백엔드 실제 응답:
```json
{
  "success": true,
  "data": {
    "regions": [...]
  },
  "error": null
}
```

#### 프론트엔드 기대 타입:
```typescript
interface RegionsResponse {
  regions: Region[];
}
```

**영향**: 타입 불일치로 인한 런타임 에러 발생 가능

**해결 방법**:
```typescript
// src/types/region.ts 수정
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  error: string | null;
}

export interface RegionsData {
  regions: Region[];
}

export type RegionsResponse = ApiResponse<RegionsData>;

// src/lib/api.ts 수정
export async function fetchRegions(): Promise<RegionsData> {
  const response = await fetch(`${API_BASE_URL}/v1/regions`);
  if (!response.ok) {
    throw new Error(`Failed to fetch regions: ${response.statusText}`);
  }

  const apiResponse: RegionsResponse = await response.json();
  if (!apiResponse.success) {
    throw new Error(apiResponse.error || 'Unknown error');
  }

  return apiResponse.data;
}
```

---

### 3. Region 필드 불일치
**문제**: 백엔드 Region 객체와 프론트엔드 Region 타입의 필드가 다름

#### 백엔드 Region 필드:
```json
{
  "id": "gangnam",
  "name": "강남",
  "city": "seoul",
  "description": "트렌디하고 세련된 분위기...",
  "keywords": ["트렌디", "쇼핑", "고급", "세련됨", "핫플레이스"],
  "centerLat": 37.4979,
  "centerLng": 127.0276
}
```

#### 프론트엔드 Region 타입:
```typescript
interface Region {
  id: string;
  name: string;
  areaType: AreaType;  // 백엔드에는 없음
  keywords: string[];
}
```

**누락 필드**:
- ❌ `description`: 권역 설명
- ❌ `centerLat`, `centerLng`: 지도 좌표
- ❌ `city`: "seoul" | "gyeonggi"

**잘못된 필드**:
- ❌ `areaType`: 백엔드는 `city` 사용

**해결 방법**:
```typescript
// src/types/region.ts 수정
export type AreaType = 'SEOUL' | 'GYEONGGI';
export type CityType = 'seoul' | 'gyeonggi';

export interface Region {
  id: string;
  name: string;
  city: CityType;                    // 백엔드 필드명 사용
  description: string;               // 추가
  keywords: string[];
  centerLat: number;                 // 추가
  centerLng: number;                 // 추가
}

// Helper function for UI
export function getAreaType(city: CityType): AreaType {
  return city.toUpperCase() as AreaType;
}
```

---

### 4. city 값 대소문자 불일치
**문제**: 백엔드는 소문자, 프론트엔드는 대문자 사용

- **백엔드**: `"seoul"`, `"gyeonggi"`
- **프론트엔드**: `"SEOUL"`, `"GYEONGGI"`

**영향**: 탭 필터링 로직 동작 불가

**해결 방법**:
```typescript
// src/components/region/RegionBottomSheet.tsx 수정
const currentRegions = useMemo(() => {
  const regionsByTab = MOCK_REGIONS.filter(
    region => region.city.toUpperCase() === activeTab
  );
  return filterRegions(regionsByTab, searchQuery);
}, [activeTab, searchQuery]);
```

---

## ✅ 정상 동작 확인 사항

### 1. 백엔드 API 엔드포인트
- ✅ `GET /v1/regions` - 200 OK
- ✅ `GET /health` - 200 OK
- ✅ 응답 시간: ~50ms
- ✅ 30개 권역 데이터 반환

### 2. 데이터 품질
- ✅ 모든 권역에 필수 필드 포함
- ✅ 키워드 배열 정상
- ✅ 좌표 데이터 유효
- ✅ 한글 인코딩 정상

---

## 🔧 권장 수정 작업

### 우선순위 1 (필수) - API 연동을 위한 필수 수정
1. **API URL 수정**: `src/lib/api.ts` 포트 8080으로 변경
2. **응답 타입 수정**: ApiResponse wrapper 추가
3. **Region 타입 수정**: 백엔드 필드 추가 (description, centerLat, centerLng)
4. **city 필드 매핑**: areaType → city 변경 및 대소문자 변환 로직

### 우선순위 2 (개선) - UI 기능 향상
1. **지도 컴포넌트**: centerLat, centerLng 활용
2. **권역 설명**: description 필드 UI에 표시
3. **에러 핸들링**: API 에러 메시지 사용자에게 표시

### 우선순위 3 (최적화) - 개발 경험 개선
1. **환경변수 설정**: `.env.local` 파일 생성
2. **타입 자동 생성**: OpenAPI/Swagger로 타입 자동화
3. **API 모킹**: MSW 설정으로 백엔드 독립 개발

---

## 📝 수정 체크리스트

### Frontend 수정 필요
- [ ] `src/lib/api.ts` - API_BASE_URL 포트 수정
- [ ] `src/types/region.ts` - Region 타입 필드 추가
- [ ] `src/types/region.ts` - ApiResponse wrapper 추가
- [ ] `src/lib/api.ts` - fetchRegions 응답 파싱 로직 수정
- [ ] `src/components/region/RegionBottomSheet.tsx` - city 필드 매핑
- [ ] `src/constants/regions.ts` - MOCK 데이터 필드 추가

### Backend 협의 필요 (대안)
- [ ] `city` 필드를 `areaType`으로 변경?
- [ ] 값을 대문자로 변경? (seoul → SEOUL)
- [ ] 응답 구조 간소화? (wrapper 제거)

---

## 🧪 테스트 시나리오

### API 통합 테스트
```typescript
describe('fetchRegions API', () => {
  it('should fetch regions successfully', async () => {
    const data = await fetchRegions();
    expect(data.regions).toHaveLength(30);
    expect(data.regions[0]).toHaveProperty('description');
    expect(data.regions[0]).toHaveProperty('centerLat');
  });

  it('should handle API errors', async () => {
    // Mock failed response
    await expect(fetchRegions()).rejects.toThrow();
  });
});
```

### UI 통합 테스트
```typescript
describe('RegionBottomSheet with real API', () => {
  it('should load and display regions', async () => {
    render(<RegionBottomSheet isOpen={true} />);

    // Wait for API call
    await waitFor(() => {
      expect(screen.getByText('강남')).toBeInTheDocument();
    });

    // Check region count
    const regions = screen.getAllByRole('button', { name: /권역/i });
    expect(regions).toHaveLength(30);
  });
});
```

---

## 📊 영향 분석

### 현재 상태
- 🔴 **API 연동 불가**: 포트 불일치로 API 호출 실패
- 🔴 **타입 에러**: 런타임에서 필드 접근 시 undefined
- 🔴 **기능 미작동**: 탭 필터링, 권역 검색 불가

### 수정 후 예상 상태
- ✅ **API 연동 성공**: 백엔드에서 실제 데이터 로드
- ✅ **타입 안전성**: 모든 필드 타입 체크 통과
- ✅ **기능 정상 동작**: 지역 선택, 검색, 필터링 작동

---

## 💡 추천 작업 순서

1. **즉시 수정 (5분)**:
   ```bash
   # .env.local 생성
   echo "NEXT_PUBLIC_API_URL=http://localhost:8080" > .env.local
   ```

2. **타입 수정 (15분)**:
   - `src/types/region.ts` 파일 수정
   - ApiResponse wrapper 추가
   - Region 필드 추가

3. **API 클라이언트 수정 (10분)**:
   - `src/lib/api.ts` 응답 파싱 로직 수정

4. **컴포넌트 수정 (20분)**:
   - RegionBottomSheet city 매핑
   - MOCK 데이터 필드 추가

5. **테스트 (10분)**:
   - 브라우저에서 실제 API 연동 확인
   - 권역 선택, 검색, 필터링 테스트

**총 예상 시간**: 약 1시간

---

**리포트 생성**: 2026-01-24 21:20 KST
**담당자**: Claude Sonnet 4.5
**다음 단계**: 프론트엔드 타입 및 API 클라이언트 수정
