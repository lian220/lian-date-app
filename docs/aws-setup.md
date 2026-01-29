# AWS 계정 초기 설정 가이드

LAD-87: AWS 계정 초기 설정 및 GitHub Actions 배포 인프라 구축

## 목차

1. [AWS 계정 기본 설정](#1-aws-계정-기본-설정)
2. [GitHub Actions용 OIDC Provider 설정](#2-github-actions용-oidc-provider-설정)
3. [IAM Role 및 정책 설정](#3-iam-role-및-정책-설정)
4. [GitHub Secrets 등록](#4-github-secrets-등록)
5. [검증](#5-검증)

---

## 1. AWS 계정 기본 설정

### 1.1 리전 설정

기본 리전: **us-east-1** (미국 동부 버지니아)

### 1.2 Root 계정 MFA 활성화

**중요**: Root 계정 보안을 위해 MFA(Multi-Factor Authentication)를 반드시 활성화해야 합니다.

#### 단계:
1. AWS Management Console에 Root 계정으로 로그인
2. 우측 상단 계정 이름 클릭 → **Security credentials** 선택
3. **Multi-factor authentication (MFA)** 섹션에서 **Assign MFA device** 클릭
4. MFA 디바이스 선택:
   - **Authenticator app** (권장): Google Authenticator, Authy 등
   - **Security Key**: YubiKey 등 하드웨어 토큰
   - **Hardware TOTP token**: 물리적 MFA 디바이스
5. 화면의 지시에 따라 설정 완료

### 1.3 Billing Alerts 설정

예상치 못한 AWS 비용 발생을 방지하기 위해 알림을 설정합니다.

#### 단계:
1. AWS Management Console → **Billing and Cost Management**
2. 좌측 메뉴에서 **Billing preferences** 클릭
3. **Receive Billing Alerts** 체크박스 활성화
4. **Save preferences** 클릭
5. **CloudWatch**로 이동하여 알림 설정:
   - Service: **CloudWatch** → **Alarms** → **Billing**
   - **Create alarm** 클릭
   - Metric: **Total Estimated Charge**
   - Threshold: 원하는 금액 설정 (예: $10, $50, $100)
   - SNS Topic 생성 및 이메일 주소 등록
   - 알림 확인 이메일의 구독 승인 링크 클릭

---

## 2. GitHub Actions용 OIDC Provider 설정

OIDC(OpenID Connect)를 사용하면 AWS Access Key를 GitHub Secrets에 저장하지 않고도 안전하게 인증할 수 있습니다.

### 2.1 OIDC Provider 생성

#### AWS Management Console:
1. **IAM** 서비스로 이동
2. 좌측 메뉴에서 **Identity providers** 클릭
3. **Add provider** 클릭
4. 다음 정보 입력:
   - **Provider type**: OpenID Connect
   - **Provider URL**: `https://token.actions.githubusercontent.com`
   - **Audience**: `sts.amazonaws.com`
5. **Add provider** 클릭

#### AWS CLI (선택사항):
```bash
aws iam create-open-id-connect-provider \
  --url https://token.actions.githubusercontent.com \
  --client-id-list sts.amazonaws.com \
  --thumbprint-list 6938fd4d98bab03faadb97b34396831e3780aea1
```

### 2.2 Trust Relationship 확인

생성된 OIDC Provider의 ARN을 기록해둡니다:
```
arn:aws:iam::{AWS_ACCOUNT_ID}:oidc-provider/token.actions.githubusercontent.com
```

---

## 3. IAM Role 및 정책 설정

### 3.1 IAM Policy 생성

GitHub Actions에서 사용할 권한을 정의한 정책을 생성합니다.

#### AWS Management Console:
1. **IAM** → **Policies** → **Create policy**
2. **JSON** 탭 선택
3. `terraform/policies/github-actions-policy.json` 파일의 내용을 붙여넣기
4. **Next: Tags** 클릭
5. (선택사항) 태그 추가
6. **Next: Review** 클릭
7. Policy 이름 입력: `GitHubActionsDeploymentPolicy`
8. Description: "Policy for GitHub Actions to deploy to ECR and ECS"
9. **Create policy** 클릭

#### 정책 요약:
- **ECR**: Docker 이미지 푸시/풀 권한
- **ECS**: 서비스 업데이트, Task Definition 등록 권한
- **CloudWatch Logs**: 로그 생성 및 기록 권한
- **IAM PassRole**: ECS Task에 IAM Role 전달 권한

### 3.2 IAM Role 생성

#### AWS Management Console:
1. **IAM** → **Roles** → **Create role**
2. **Trusted entity type**: Web identity
3. **Identity provider**: `token.actions.githubusercontent.com` 선택
4. **Audience**: `sts.amazonaws.com` 선택
5. **GitHub organization**: 본인의 GitHub 조직 또는 사용자명 입력 (예: `lian220`)
6. **GitHub repository**: 리포지토리 이름 입력 (예: `lian-date-app`)
7. **GitHub branch**: (선택사항) 특정 브랜치만 허용하려면 입력 (예: `main`)
8. **Next** 클릭
9. **Permissions policies**: 위에서 생성한 `GitHubActionsDeploymentPolicy` 선택
10. **Next** 클릭
11. Role name: `github-actions-deployment-role`
12. Description: "Role for GitHub Actions to deploy lian-date-app to AWS"
13. **Create role** 클릭

### 3.3 Trust Relationship 수정 (필요 시)

생성된 Role의 Trust Relationship을 확인하고 필요 시 수정합니다.

#### Trust Policy 예시:
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::{AWS_ACCOUNT_ID}:oidc-provider/token.actions.githubusercontent.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com"
        },
        "StringLike": {
          "token.actions.githubusercontent.com:sub": "repo:{GITHUB_ORG}/{REPO_NAME}:*"
        }
      }
    }
  ]
}
```

**참고**: `{AWS_ACCOUNT_ID}`, `{GITHUB_ORG}`, `{REPO_NAME}`을 실제 값으로 대체합니다.

### 3.4 Role ARN 기록

생성된 Role의 ARN을 기록해둡니다:
```
arn:aws:iam::{AWS_ACCOUNT_ID}:role/github-actions-deployment-role
```

---

## 4. GitHub Secrets 등록

GitHub Actions에서 사용할 AWS 정보를 GitHub Secrets에 등록합니다.

### 4.1 GitHub Secrets 추가

1. GitHub 리포지토리로 이동
2. **Settings** → **Secrets and variables** → **Actions**
3. **New repository secret** 클릭
4. 다음 Secrets를 추가:

| Secret Name | Value | 설명 |
|------------|-------|------|
| `AWS_ACCOUNT_ID` | `123456789012` | AWS 계정 ID (12자리 숫자) |
| `AWS_REGION` | `us-east-1` | AWS 리전 |
| `AWS_ROLE_ARN` | `arn:aws:iam::123456789012:role/github-actions-deployment-role` | IAM Role ARN |

### 4.2 AWS 계정 ID 확인 방법

#### AWS Management Console:
- 우측 상단 계정 이름 클릭 시 표시됨

#### AWS CLI:
```bash
aws sts get-caller-identity --query Account --output text
```

---

## 5. 검증

### 5.1 OIDC Provider 확인

```bash
aws iam list-open-id-connect-providers
```

예상 출력:
```json
{
  "OpenIDConnectProviderList": [
    {
      "Arn": "arn:aws:iam::123456789012:oidc-provider/token.actions.githubusercontent.com"
    }
  ]
}
```

### 5.2 IAM Role 확인

```bash
aws iam get-role --role-name github-actions-deployment-role
```

### 5.3 GitHub Actions에서 테스트

간단한 GitHub Actions workflow를 생성하여 AWS 인증을 테스트합니다.

#### `.github/workflows/test-aws-auth.yml`:
```yaml
name: Test AWS Authentication

on:
  workflow_dispatch:

permissions:
  id-token: write
  contents: read

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          role-to-assume: ${{ secrets.AWS_ROLE_ARN }}
          aws-region: ${{ secrets.AWS_REGION }}

      - name: Verify AWS identity
        run: |
          aws sts get-caller-identity
          echo "✅ AWS authentication successful!"
```

### 5.4 테스트 실행

1. GitHub 리포지토리의 **Actions** 탭으로 이동
2. **Test AWS Authentication** workflow 선택
3. **Run workflow** 클릭
4. Workflow 실행 결과 확인:
   - ✅ 성공: AWS 인증이 정상적으로 작동함
   - ❌ 실패: Trust Relationship 또는 Secrets 확인 필요

---

## 다음 단계

- [ ] Terraform 인프라 코드 작성
- [ ] ECR Repository 생성
- [ ] ECS Cluster 및 Service 구성
- [ ] GitHub Actions 배포 workflow 작성

---

## 참고 자료

- [AWS OIDC for GitHub Actions 공식 문서](https://docs.github.com/en/actions/deployment/security-hardening-your-deployments/configuring-openid-connect-in-amazon-web-services)
- [AWS IAM Best Practices](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html)
- [GitHub Actions 보안 강화](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)

---

**작성일**: 2026-01-26
**작성자**: LAD-87
**최종 수정일**: 2026-01-26
