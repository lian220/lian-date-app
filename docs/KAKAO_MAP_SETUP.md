# 카카오맵 SDK 설정 가이드

## 개요

이 프로젝트는 **카카오맵 JavaScript SDK**를 활용하여 지도 기능을 제공합니다.

## 구현 내용

### ✅ 완료된 기능

- [x] 카카오맵 JavaScript SDK 연동
- [x] 지도 렌더링 컴포넌트
- [x] 줌 인/아웃 컨트롤
- [x] 드래그 이동 기능
- [x] 지도 영역 자동 맞춤 (모든 마커 포함)
- [x] 마커 표시 및 클릭 이벤트

## 설정 방법

### 1. 카카오 디벨로퍼스 앱 키 발급

1. [카카오 디벨로퍼스](https://developers.kakao.com) 방문
2. 회원가입 또는 로그인
3. 좌측 메뉴에서 "내 애플리케이션" 클릭
4. "앱 만들기" 버튼 클릭
5. 앱 이름 입력 후 생성
6. "앱 키" 탭에서 **JavaScript 키** 복사

### 2. 환경 변수 설정

`frontend/.env.local` 파일에 다음을 추가하세요:

```env
NEXT_PUBLIC_KAKAO_MAP_API_KEY=YOUR_JAVASCRIPT_KEY_HERE
```

**주의**: `NEXT_PUBLIC_` 접두사가 필요합니다. 이를 통해 클라이언트 사이드에서 접근 가능합니다.

### 3. 플랫폼 설정

카카오 디벨로퍼스에서 앱 설정으로 이동하여:

1. "플랫폼" 섹션에서 "Web" 추가
2. 사이트 도메인 입력:
   - 로컬: `http://localhost:3000` (또는 `3001`)
   - 프로덕션: 실제 도메인

## 컴포넌트 사용법

### 기본 사용

```tsx
import KakaoMapComponent from '@/components/KakaoMap';
import type { MapPlace } from '@/types/kakao-map';

const places: MapPlace[] = [
  {
    id: 'place-1',
    name: '서울시청',
    lat: 37.5665,
    lng: 126.978,
    address: '서울 중구 세종대로 110',
    category: '시청',
  },
];

export default function MyMap() {
  return (
    <KakaoMapComponent
      places={places}
      center={{ lat: 37.5665, lng: 126.978 }}
      zoom={3}
      onMarkerClick={(place) => console.log(place)}
    />
  );
}
```

### Props 설명

| Props | 타입 | 설명 | 기본값 |
|-------|------|------|--------|
| `places` | `MapPlace[]` | 표시할 장소 배열 | 필수 |
| `center` | `{ lat, lng }` | 지도 중심 좌표 | 서울 (37.5665, 126.978) |
| `zoom` | `number` | 초기 줌 레벨 (1-14) | 3 |
| `onMapReady` | `(map) => void` | 지도 준비 완료 콜백 | - |
| `onMarkerClick` | `(place) => void` | 마커 클릭 콜백 | - |

## 주요 기능

### 1. 줌 인/아웃 컨트롤

- 우측 하단의 + / - 버튼으로 조작
- 프로그래매틱으로: `map.setLevel(level)`
- 레벨 범위: 1 (전체) ~ 14 (상세)

### 2. 드래그 이동

- 마우스로 지도를 드래그하여 이동
- 기본적으로 활성화됨

### 3. 마커 클릭

- 마커를 클릭하면 `onMarkerClick` 콜백 실행
- 장소 정보 표시 가능

### 4. 자동 영역 맞춤

- `LatLngBounds`를 사용하여 모든 마커를 포함하는 영역으로 자동 조정
- `places` 변경 시 자동으로 업데이트

## 파일 구조

```
frontend/
├── src/
│   ├── components/
│   │   └── KakaoMap.tsx          # 카카오맵 컴포넌트
│   ├── types/
│   │   └── kakao-map.ts          # 타입 정의
│   └── app/
│       └── map/
│           └── page.tsx           # 지도 페이지 (예제)
├── .env.example                   # 환경 변수 예제
└── .env.local                     # 실제 환경 변수 (git 제외)
```

## 테스트

### 개발 환경에서 테스트

```bash
cd frontend
npm run dev
```

브라우저에서 `http://localhost:3000/map` 접속

## 트러블슈팅

### "Kakao Maps SDK failed to load"

**원인**: API 키가 올바르지 않거나 설정되지 않음

**해결책**:
1. `.env.local`에 올바른 API 키가 있는지 확인
2. 카카오 디벨로퍼스에서 JavaScript 키 사용 여부 확인
3. 도메인 설정이 맞는지 확인

### 지도가 표시되지 않음

**원인**: 컨테이너의 높이가 설정되지 않음

**해결책**: KakaoMapComponent 부모 요소에 높이 지정

```tsx
<div style={{ height: '600px' }}>
  <KakaoMapComponent {...props} />
</div>
```

### 마커가 클릭되지 않음

**원인**: 마커의 클릭 이벤트 리스너 미등록

**확인 사항**: `onMarkerClick` prop이 전달되었는지 확인

## 참고 자료

- [카카오맵 API 문서](https://apis.map.kakao.com/)
- [JavaScript SDK 가이드](https://apis.map.kakao.com/web/)
- [카카오 디벨로퍼스](https://developers.kakao.com)
