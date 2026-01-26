# GitHub Secrets 설정 가이드

CI/CD 파이프라인 실행을 위해 필요한 GitHub Secrets를 설정합니다.

## 필수 Secrets

### AWS 인증 (OIDC 사용 - 권장)

**AWS_ROLE_ARN**
- GitHub Actions에서 사용할 AWS IAM Role ARN
- 형식: `arn:aws:iam::ACCOUNT_ID:role/github-actions-deployment-role`

### Codecov (선택)

**CODECOV_TOKEN**
- 테스트 커버리지 리포트 업로드
- 발급: https://codecov.io

## GitHub OIDC Provider 설정

### 1. AWS IAM Identity Provider 생성
- Provider URL: `https://token.actions.githubusercontent.com`
- Audience: `sts.amazonaws.com`

### 2. IAM Role 생성
- Trust Policy: GitHub repository 제한
- Permissions: ECR, ECS, CloudWatch Logs

### 3. GitHub Secrets 추가
```
Settings → Secrets and variables → Actions → New repository secret
```

## 참고 자료
- [GitHub OIDC 설정](https://docs.github.com/en/actions/deployment/security-hardening-your-deployments/configuring-openid-connect-in-amazon-web-services)
