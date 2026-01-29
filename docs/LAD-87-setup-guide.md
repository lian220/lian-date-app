# LAD-87 설정 가이드

AWS 계정 초기 설정 및 GitHub Actions 배포 환경 구축 전체 가이드

## 🎯 목표

Terraform 없이 수동으로 AWS OIDC Provider와 IAM Role을 설정하여 GitHub Actions에서 안전하게 AWS에 배포할 수 있도록 환경을 구축합니다.

## 📋 전제 조건

- AWS 계정 (Root 계정 접근 권한)
- AWS CLI 설치 및 구성
- GitHub 리포지토리 쓰기 권한
- (선택) GitHub CLI 설치

## 🚀 빠른 시작 (Quick Start)

### 1단계: AWS 기본 설정

상세 가이드: [docs/aws-setup.md](./aws-setup.md)

```bash
# 1.1 Root 계정 MFA 활성화
# AWS Console에서 수동 설정 (docs/aws-setup.md 참고)

# 1.2 Billing Alerts 설정
# AWS Console에서 수동 설정 (docs/aws-setup.md 참고)
```

### 2단계: AWS OIDC Provider 및 IAM Role 생성

```bash
# 스크립트 실행
GITHUB_ORG=lian220 \
GITHUB_REPO=lian-date-app \
./scripts/setup-aws-oidc.sh
```

**스크립트가 수행하는 작업:**
- ✅ OIDC Provider 생성 (`token.actions.githubusercontent.com`)
- ✅ IAM Policy 생성 (`GitHubActionsDeploymentPolicy`)
- ✅ IAM Role 생성 (`github-actions-deployment-role`)
- ✅ Policy와 Role 연결

**예상 출력:**
```
======================================
설정 완료!
======================================

다음 정보를 GitHub Secrets에 등록하세요:

AWS_ACCOUNT_ID     = 123456789012
AWS_REGION         = us-east-1
AWS_ROLE_ARN       = arn:aws:iam::123456789012:role/github-actions-deployment-role
```

### 3단계: GitHub Secrets 등록

상세 가이드: [docs/github-secrets-setup.md](./github-secrets-setup.md)

#### 웹사이트에서 등록:
1. GitHub 리포지토리 → Settings → Secrets and variables → Actions
2. New repository secret 클릭하여 각각 추가:
   - `AWS_ACCOUNT_ID`: `123456789012` (12자리 숫자)
   - `AWS_REGION`: `us-east-1`
   - `AWS_ROLE_ARN`: `arn:aws:iam::123456789012:role/github-actions-deployment-role`

#### GitHub CLI로 등록:
```bash
REPO="lian220/lian-date-app"  # 실제 값으로 변경
AWS_ACCOUNT_ID="123456789012"  # 실제 값으로 변경
AWS_ROLE_ARN="arn:aws:iam::123456789012:role/github-actions-deployment-role"  # 실제 값으로 변경

gh secret set AWS_ACCOUNT_ID --body "$AWS_ACCOUNT_ID" --repo $REPO
gh secret set AWS_REGION --body "us-east-1" --repo $REPO
gh secret set AWS_ROLE_ARN --body "$AWS_ROLE_ARN" --repo $REPO
```

### 4단계: 검증

```bash
# GitHub Actions workflow 실행
# GitHub 리포지토리 → Actions → Test AWS Authentication → Run workflow
```

**성공 시 출력:**
```
✅ AWS authentication successful!
✅ ECR access verified!
✅ ECS access verified!
✅ CloudWatch Logs access verified!
✅ All AWS access tests passed!
```

## 📁 생성된 파일

```
.
├── .github/workflows/
│   └── test-aws-auth.yml              # AWS 인증 테스트 workflow
├── docs/
│   ├── aws-setup.md                   # AWS 설정 상세 가이드
│   ├── github-secrets-setup.md        # GitHub Secrets 설정 가이드
│   └── LAD-87-setup-guide.md          # 이 파일
├── scripts/
│   └── setup-aws-oidc.sh              # OIDC Provider/IAM Role 생성 스크립트
├── terraform/policies/
│   └── github-actions-policy.json     # IAM Policy 정의
├── .env.aws.example                   # AWS 환경 변수 템플릿
└── .gitignore                         # .env.aws 추가됨
```

## 🔧 수동 설정 (스크립트 사용 안 함)

스크립트 대신 AWS Management Console에서 수동으로 설정하려면:

1. **OIDC Provider 생성**
   - IAM → Identity providers → Add provider
   - Provider URL: `https://token.actions.githubusercontent.com`
   - Audience: `sts.amazonaws.com`

2. **IAM Policy 생성**
   - IAM → Policies → Create policy
   - `terraform/policies/github-actions-policy.json` 내용 사용
   - Policy 이름: `GitHubActionsDeploymentPolicy`

3. **IAM Role 생성**
   - IAM → Roles → Create role
   - Trusted entity: Web identity
   - Identity provider: `token.actions.githubusercontent.com`
   - Audience: `sts.amazonaws.com`
   - Attach policy: `GitHubActionsDeploymentPolicy`
   - Role 이름: `github-actions-deployment-role`

상세 단계는 [docs/aws-setup.md](./aws-setup.md) 참고

## ✅ 체크리스트

### AWS 설정
- [ ] Root 계정 MFA 활성화
- [ ] Billing Alerts 설정
- [ ] OIDC Provider 생성 확인
- [ ] IAM Policy 생성 확인
- [ ] IAM Role 생성 확인
- [ ] Policy와 Role 연결 확인

### GitHub 설정
- [ ] AWS_ACCOUNT_ID Secret 등록
- [ ] AWS_REGION Secret 등록
- [ ] AWS_ROLE_ARN Secret 등록
- [ ] Secrets 등록 확인 (3개)

### 검증
- [ ] Test AWS Authentication workflow 실행
- [ ] AWS 인증 성공 확인
- [ ] ECR 접근 권한 확인
- [ ] ECS 접근 권한 확인
- [ ] CloudWatch Logs 접근 권한 확인

## 🔍 트러블슈팅

### 문제: "Could not assume role"
**원인**: IAM Role의 Trust Relationship 설정 오류

**해결**:
```bash
# Trust Relationship 확인
aws iam get-role --role-name github-actions-deployment-role --query 'Role.AssumeRolePolicyDocument'

# 스크립트 재실행
GITHUB_ORG=lian220 GITHUB_REPO=lian-date-app ./scripts/setup-aws-oidc.sh
```

### 문제: "Access Denied" for ECR/ECS
**원인**: IAM Policy 권한 부족

**해결**:
```bash
# Policy 연결 확인
aws iam list-attached-role-policies --role-name github-actions-deployment-role

# Policy가 연결되지 않았다면 재연결
aws iam attach-role-policy \
  --role-name github-actions-deployment-role \
  --policy-arn arn:aws:iam::${AWS_ACCOUNT_ID}:policy/GitHubActionsDeploymentPolicy
```

### 문제: GitHub Secrets가 작동하지 않음
**원인**: Secret 이름 또는 값 오류

**해결**:
```bash
# Secrets 목록 확인
gh secret list --repo lian220/lian-date-app

# Secret 재등록
gh secret set AWS_ROLE_ARN --body "arn:aws:iam::123456789012:role/github-actions-deployment-role" --repo lian220/lian-date-app
```

## 📚 참고 문서

- [AWS OIDC for GitHub Actions](https://docs.github.com/en/actions/deployment/security-hardening-your-deployments/configuring-openid-connect-in-amazon-web-services)
- [AWS IAM Best Practices](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html)
- [GitHub Actions Security](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)

## 🎯 다음 단계

LAD-87 설정이 완료되면:
1. **ECR Repository 생성** - Docker 이미지 저장소
2. **ECS Cluster 구성** - 컨테이너 실행 환경
3. **배포 Workflow 작성** - 자동 배포 파이프라인
4. **애플리케이션 배포** - 실제 서비스 배포

---

**티켓**: LAD-87
**작성일**: 2026-01-26
**상태**: 완료
