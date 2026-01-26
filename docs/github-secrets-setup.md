# GitHub Secrets 설정 가이드

LAD-87: GitHub Actions에서 AWS 배포를 위한 Secrets 설정

## 목차

1. [필수 Secrets](#1-필수-secrets)
2. [Secrets 등록 방법](#2-secrets-등록-방법)
3. [값 확인 방법](#3-값-확인-방법)
4. [검증](#4-검증)

---

## 1. 필수 Secrets

GitHub Actions에서 AWS에 배포하려면 다음 3개의 Secrets가 필요합니다:

| Secret Name | 설명 | 예시 |
|------------|------|------|
| `AWS_ACCOUNT_ID` | AWS 계정 ID (12자리 숫자) | `123456789012` |
| `AWS_REGION` | AWS 리전 | `us-east-1` |
| `AWS_ROLE_ARN` | GitHub Actions용 IAM Role ARN | `arn:aws:iam::123456789012:role/github-actions-deployment-role` |

---

## 2. Secrets 등록 방법

### 2.1 GitHub 웹사이트에서 등록

1. **GitHub 리포지토리로 이동**
   ```
   https://github.com/{organization}/{repository}
   ```

2. **Settings 탭 클릭**
   - 리포지토리 상단 메뉴에서 `Settings` 클릭
   - (주의: 리포지토리 쓰기 권한이 필요합니다)

3. **Secrets and variables 메뉴로 이동**
   - 좌측 사이드바에서 `Secrets and variables` → `Actions` 클릭

4. **각 Secret 추가**
   - `New repository secret` 버튼 클릭
   - Name: Secret 이름 입력 (예: `AWS_ACCOUNT_ID`)
   - Secret: 값 입력 (예: `123456789012`)
   - `Add secret` 버튼 클릭
   - 나머지 Secrets도 동일하게 반복

### 2.2 GitHub CLI로 등록 (선택사항)

GitHub CLI(`gh`)를 사용하면 커맨드라인에서 Secrets를 등록할 수 있습니다.

#### 사전 준비:
```bash
# GitHub CLI 설치 확인
gh --version

# GitHub 인증 (처음 한 번만)
gh auth login
```

#### Secrets 등록:
```bash
# Repository 이름 설정
REPO="organization/repository"  # 실제 값으로 변경

# AWS_ACCOUNT_ID 등록
gh secret set AWS_ACCOUNT_ID --body "123456789012" --repo $REPO

# AWS_REGION 등록
gh secret set AWS_REGION --body "us-east-1" --repo $REPO

# AWS_ROLE_ARN 등록
gh secret set AWS_ROLE_ARN --body "arn:aws:iam::123456789012:role/github-actions-deployment-role" --repo $REPO
```

#### 등록 확인:
```bash
gh secret list --repo $REPO
```

---

## 3. 값 확인 방법

### 3.1 AWS_ACCOUNT_ID 확인

#### AWS Management Console:
- 우측 상단 계정 이름 클릭 시 표시됨

#### AWS CLI:
```bash
aws sts get-caller-identity --query Account --output text
```

### 3.2 AWS_REGION 확인

프로젝트에서 사용하는 기본 리전:
```
us-east-1
```

### 3.3 AWS_ROLE_ARN 확인

#### 스크립트 실행 후 출력된 값 사용:
```bash
# setup-aws-oidc.sh 실행 시 출력된 ARN 사용
```

#### AWS CLI로 확인:
```bash
aws iam get-role --role-name github-actions-deployment-role --query 'Role.Arn' --output text
```

예상 형식:
```
arn:aws:iam::123456789012:role/github-actions-deployment-role
```

---

## 4. 검증

### 4.1 Secrets 등록 확인

#### GitHub 웹사이트:
1. Settings → Secrets and variables → Actions
2. Repository secrets 섹션에서 3개의 Secrets 확인:
   - ✅ AWS_ACCOUNT_ID
   - ✅ AWS_REGION
   - ✅ AWS_ROLE_ARN

#### GitHub CLI:
```bash
gh secret list --repo organization/repository
```

예상 출력:
```
AWS_ACCOUNT_ID   Updated YYYY-MM-DD
AWS_REGION       Updated YYYY-MM-DD
AWS_ROLE_ARN     Updated YYYY-MM-DD
```

### 4.2 GitHub Actions에서 테스트

1. **Test workflow 실행**
   - GitHub 리포지토리의 `Actions` 탭으로 이동
   - `Test AWS Authentication` workflow 선택
   - `Run workflow` 클릭
   - 브랜치 선택 (기본값: main)
   - `Run workflow` 버튼 클릭

2. **결과 확인**
   - Workflow 실행 로그 확인
   - 각 단계별 성공 여부 확인:
     - ✅ Configure AWS credentials
     - ✅ Verify AWS identity
     - ✅ Test ECR access
     - ✅ Test ECS access
     - ✅ Test CloudWatch Logs access

3. **성공 시 예상 출력**
   ```
   ✅ AWS authentication successful!
   ✅ ECR access verified!
   ✅ ECS access verified!
   ✅ CloudWatch Logs access verified!
   ✅ All AWS access tests passed!
   ```

### 4.3 문제 해결

#### 오류: "Error: Could not assume role"
- **원인**: IAM Role의 Trust Relationship 설정 오류
- **해결**:
  1. IAM Role의 Trust Relationship 확인
  2. GitHub repository 정보가 정확한지 확인
  3. `scripts/setup-aws-oidc.sh` 재실행

#### 오류: "Error: Credentials could not be loaded"
- **원인**: Secrets가 올바르게 설정되지 않음
- **해결**:
  1. GitHub Secrets 다시 확인
  2. Secret 이름이 정확한지 확인 (대소문자 구분)
  3. Secret 값에 불필요한 공백이 없는지 확인

#### 오류: "Error: Access Denied"
- **원인**: IAM Policy 권한 부족
- **해결**:
  1. IAM Policy가 Role에 올바르게 연결되었는지 확인
  2. `terraform/policies/github-actions-policy.json` 내용 확인
  3. 필요한 권한이 모두 포함되었는지 확인

---

## 체크리스트

설정이 완료되었는지 확인하세요:

- [ ] AWS_ACCOUNT_ID Secret 등록 완료
- [ ] AWS_REGION Secret 등록 완료
- [ ] AWS_ROLE_ARN Secret 등록 완료
- [ ] GitHub Secrets 목록에서 3개 Secrets 확인 완료
- [ ] Test AWS Authentication workflow 실행 성공
- [ ] 모든 테스트 단계 통과 확인

---

## 다음 단계

Secrets 설정이 완료되면:
1. ✅ ECR Repository 생성
2. ✅ ECS Cluster 및 Service 구성
3. ✅ 배포 workflow 작성
4. ✅ 실제 애플리케이션 배포

---

**작성일**: 2026-01-26
**관련 문서**:
- [AWS 설정 가이드](./aws-setup.md)
- [GitHub Actions Workflow](./.github/workflows/test-aws-auth.yml)
