---
name: load-testing
description: k6 기반 부하 테스트 스킬. API 성능 테스트 스크립트 생성, 실행, 결과 분석. "부하 테스트", "성능 테스트", "load test", "stress test", "k6" 키워드에 반응.
---

# k6 Load Testing

## 개요
Spring Boot API 엔드포인트에 대한 부하 테스트를 k6로 수행합니다.

## 사전 요구사항
```bash
# k6 설치 (macOS)
brew install k6
```

## 테스트 스크립트 생성 규칙

### 디렉토리 구조
```
backend/load-tests/
├── scripts/
│   ├── course-create.js      # 코스 생성 API
│   ├── course-list.js        # 코스 목록 조회 API
│   └── health-check.js       # 헬스체크
├── scenarios/
│   ├── smoke.js              # 스모크 테스트 (최소 부하)
│   ├── load.js               # 일반 부하 테스트
│   └── stress.js             # 스트레스 테스트
└── config.js                 # 공통 설정
```

### 기본 스크립트 패턴
```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  stages: [
    { duration: '30s', target: 10 },   // ramp up
    { duration: '1m', target: 10 },    // steady
    { duration: '10s', target: 0 },    // ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95% 요청 < 500ms
    http_req_failed: ['rate<0.01'],    // 실패율 < 1%
  },
};

export default function () {
  const res = http.get(`${BASE_URL}/v1/courses`);
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });
  sleep(1);
}
```

### 시나리오별 설정
| 시나리오 | VUs | Duration | 목적 |
|----------|-----|----------|------|
| Smoke | 1-5 | 1분 | 기능 정상 동작 확인 |
| Load | 10-50 | 5분 | 일반 트래픽 성능 |
| Stress | 50-200 | 10분 | 한계 성능 확인 |
| Spike | 0→100→0 | 3분 | 급격한 트래픽 대응 |

## 실행 방법
```bash
# 스모크 테스트
k6 run backend/load-tests/scenarios/smoke.js

# 환경 변수로 URL 지정
k6 run -e BASE_URL=http://localhost:8080 backend/load-tests/scripts/course-list.js

# HTML 리포트 생성
k6 run --out json=result.json backend/load-tests/scenarios/load.js
```

## 결과 분석 기준
| 메트릭 | 좋음 | 주의 | 나쁨 |
|--------|------|------|------|
| p95 응답시간 | <200ms | 200-500ms | >500ms |
| p99 응답시간 | <500ms | 500ms-1s | >1s |
| 에러율 | <0.1% | 0.1-1% | >1% |
| 처리량(RPS) | 목표치 이상 | 목표치 80% | 목표치 미달 |

## 상세 가이드
- 시나리오 작성법: [references/scenarios.md](references/scenarios.md)
