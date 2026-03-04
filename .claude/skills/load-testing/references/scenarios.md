# k6 시나리오 작성 가이드

## Smoke Test
```javascript
export const options = {
  vus: 1,
  duration: '1m',
  thresholds: {
    http_req_failed: ['rate==0'],
    http_req_duration: ['p(95)<1000'],
  },
};
```

## Load Test
```javascript
export const options = {
  stages: [
    { duration: '2m', target: 20 },
    { duration: '5m', target: 20 },
    { duration: '2m', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.01'],
  },
};
```

## Stress Test
```javascript
export const options = {
  stages: [
    { duration: '2m', target: 50 },
    { duration: '5m', target: 100 },
    { duration: '2m', target: 200 },
    { duration: '5m', target: 200 },
    { duration: '2m', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000'],
    http_req_failed: ['rate<0.05'],
  },
};
```

## Spike Test
```javascript
export const options = {
  stages: [
    { duration: '10s', target: 100 },
    { duration: '1m', target: 100 },
    { duration: '10s', target: 0 },
  ],
};
```

## POST 요청 예시 (코스 생성)
```javascript
import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  const payload = JSON.stringify({
    region: '강남',
    budget: 50000,
    dateType: 'CASUAL',
  });

  const params = {
    headers: { 'Content-Type': 'application/json' },
  };

  const res = http.post(`${BASE_URL}/v1/courses`, payload, params);
  check(res, {
    'status is 201': (r) => r.status === 201,
    'has course id': (r) => JSON.parse(r.body).id !== undefined,
  });
}
```

## 인증이 필요한 API
```javascript
import http from 'k6/http';

export function setup() {
  const loginRes = http.post(`${BASE_URL}/v1/auth/login`, JSON.stringify({
    email: 'test@example.com',
    password: 'password',
  }), { headers: { 'Content-Type': 'application/json' } });

  return { token: JSON.parse(loginRes.body).token };
}

export default function (data) {
  const params = {
    headers: {
      'Authorization': `Bearer ${data.token}`,
      'Content-Type': 'application/json',
    },
  };
  http.get(`${BASE_URL}/v1/courses`, params);
}
```
