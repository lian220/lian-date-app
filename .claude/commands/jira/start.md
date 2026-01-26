# Jira 티켓 시작

**사용법**: `jira-start LAD-42 [브랜치명]`
- 브랜치명 생략 시 티켓 제목으로 자동 생성 (예: `feature/LAD-42-implement-region-map`)

티켓 ID와 브랜치명을 받아 개발 환경을 설정합니다.

## 인자
- 첫 번째: Jira 티켓 ID (필수, 예: LAD-42)
- 두 번째: 브랜치명 (선택, 예: region-selection-map)
  - 없으면 티켓 제목 기반으로 자동 생성

## 실행 내용

$ARGUMENTS

### 단계 1: 티켓 정보 확인
1. Jira 티켓 정보 조회 (mcp__atlassian 사용)
2. 티켓 제목, 설명, 수락 조건 확인
3. 브랜치명이 없는 경우:
   - 티켓 제목을 kebab-case로 변환
   - 형식: `feature/{티켓ID}-{티켓제목-kebab-case}`
   - 예: `feature/LAD-42-implement-region-selection-map`

### 단계 2: 브랜치 생성 및 전환
1. 현재 git 상태 확인
2. main/develop 브랜치에서 최신 코드 pull
3. **브랜치 생성 규칙 (MANDATORY)**:
   - ✅ **반드시 이 형식 사용**: `feature/{티켓ID}-{티켓제목-kebab-case}`
   - ✅ 예시: `feature/LAD-42-implement-region-selection-map`
   - ❌ **절대 사용 금지**: `사용자명/티켓ID` (예: `imdoyeong/LAD-42`)
   - ❌ **절대 사용 금지**: `feature/{티켓ID}` (제목 없이 ID만)
   - 티켓 제목은 반드시 kebab-case로 변환
   - 브랜치명에 사용자 이름이나 특수문자 포함 금지
4. 브랜치로 전환
5. 관련 파일/컴포넌트 파악
6. 작업 범위 요약 제공

### 출력
- 생성된 브랜치명
- 티켓 요약 정보
- 예상 작업 파일 목록
- 다음 단계 안내
