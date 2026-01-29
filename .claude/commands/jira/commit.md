# Jira 커밋 및 진행 업데이트

**사용법**: `jira-commit LAD-42 [커밋메시지]`
- 커밋 메시지 생략 시 변경 내용 분석하여 자동 생성 (Conventional Commits 형식)

변경사항을 커밋하고 Jira 티켓 진행 상황을 업데이트합니다.

## 인자
- 첫 번째: Jira 티켓 ID (필수, 예: LAD-42)
- 두 번째: 커밋 메시지 (선택, 예: "feat: 지역 선택 맵 UI 구현")
  - 없으면 git diff 분석하여 자동 생성

## 실행 내용

$ARGUMENTS

### 단계 6: 변경사항 커밋
1. git status로 변경 파일 확인
2. git diff로 변경 내용 검토
3. 커밋 메시지가 없는 경우:
   - 변경된 파일과 diff 내용 분석
   - Conventional Commits 타입 자동 결정 (feat/fix/refactor 등)
   - scope 추출 (변경된 모듈/컴포넌트 기준)
   - 간결하고 명확한 메시지 자동 생성
   - 형식: `{type}({scope}): {message} [티켓ID]`
4. 관련 파일만 선택적 staging
5. Conventional Commit 형식으로 커밋
   - 예: `feat(map): 지역 선택 UI 구현 [LAD-42]`

### 단계 7: Jira 진행 업데이트
1. 티켓에 작업 로그 추가 (mcp__atlassian 사용)
2. 진행 상태 업데이트 (In Progress → In Review)
3. 커밋 해시 및 변경 요약 코멘트 추가

### 커밋 타입
- **feat**: 새로운 기능
- **fix**: 버그 수정
- **refactor**: 리팩토링
- **style**: 스타일 변경
- **docs**: 문서 수정
- **test**: 테스트 추가/수정
- **chore**: 기타 작업

### 출력
- 커밋 정보 (해시, 메시지)
- 변경된 파일 목록
- Jira 업데이트 상태
- 다음 단계 안내
