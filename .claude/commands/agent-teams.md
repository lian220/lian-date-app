# Agent Teams (병렬 에이전트 실행)

**사용법**: `/agent-teams [프리셋] [대상]`

여러 전문 에이전트를 병렬로 실행하여 다각도 분석을 수행합니다.

## 인자
- 첫 번째: 프리셋 이름 (필수)
- 두 번째: 분석 대상 (선택, 기본: 현재 변경사항)

$ARGUMENTS

## 프리셋

### `review` — 코드 리뷰 팀
```
병렬 실행:
├── @code-reviewer    → 코드 품질, 아키텍처 위반
├── @security-sentinel → 보안 취약점, OWASP
└── @debugger          → 잠재적 버그, 엣지 케이스
```
**사용 시점**: PR 생성 전 종합 리뷰

### `fullstack` — 풀스택 분석 팀
```
병렬 실행:
├── @backend-architect → 백엔드 아키텍처 분석
├── @code-reviewer     → 코드 품질 리뷰
└── @security-sentinel → 보안 감사
```
**사용 시점**: 새 기능 설계 검증

### `quality` — 품질 검증 팀
```
병렬 실행:
├── @code-reviewer     → 코드 품질
├── @security-sentinel → 보안
└── (load-testing)     → 성능 기준 확인
```
**사용 시점**: 릴리스 전 종합 품질 검증

### `debug` — 디버깅 팀
```
병렬 실행:
├── @debugger          → 에러 원인 분석
└── @code-reviewer     → 관련 코드 구조 문제
```
**사용 시점**: 복잡한 버그 해결

## 실행 방법

### 1. 변경사항 기반 리뷰
```
/agent-teams review
```
→ git diff 기반으로 변경된 파일을 모든 에이전트가 병렬 분석

### 2. 특정 모듈 분석
```
/agent-teams fullstack domain/course
```
→ course 도메인에 대해 풀스택 분석

### 3. 릴리스 전 검증
```
/agent-teams quality
```
→ 전체 프로젝트 품질 + 보안 + 성능 검증

## 결과 통합

각 에이전트 결과를 수집 후 통합 리포트 생성:

```
## Agent Teams Report: [프리셋]

### @code-reviewer 결과
- Critical: N개 / Warning: N개 / Suggestion: N개

### @security-sentinel 결과
- Critical: N개 / High: N개 / Medium: N개

### @debugger 결과
- 발견된 잠재 버그: N개

### 종합 판정
- ✅ PASS: 모든 에이전트 Critical 0개
- ⚠️ CONCERNS: Critical 있으나 수정 가능
- ❌ REWORK: Critical 다수, 재작업 필요
```

## 핵심 규칙
1. 모든 에이전트는 **병렬** 실행 (순차 아님)
2. 결과는 **통합 리포트**로 합산
3. Critical 이슈가 있으면 수정 후 재실행 권장
