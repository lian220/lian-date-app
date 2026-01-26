# CI/CD 설정 가이드

이 문서는 Terraform으로 생성한 AWS 리소스와 GitHub Actions CI/CD 파이프라인을 연결하는 방법을 설명합니다.

## 호환성 확인 결과

### ✅ 완벽하게 호환됨
- **ECR 리포지토리 이름**: `lian-date-prod-backend`, `lian-date-prod-frontend`
- **ECS 클러스터**: `lian-date-prod-cluster`
- **ECS 서비스**: `lian-date-prod-backend-service`, `lian-date-prod-frontend-service`
- **ALB**: `lian-date-prod-alb`
- **AWS Region**: `us-east-1`
- **Health Check 엔드포인트**: `/health`

## 설정 단계

### 1. Terraform 변수 설정

`terraform/terraform.tfvars` 파일을 생성하고 다음 내용을 추가하세요:

```hcl
# AWS Configuration
aws_region   = "us-east-1"
project_name = "lian-date"

# GitHub Configuration (중요!)
github_org  = "your-github-username-or-org"  # 실제 GitHub 사용자명 또는 조직명으로 변경
github_repo = "lian-date-app"                # 실제 리포지토리 이름으로 변경

# Database Configuration
db_username = "your-db-username"
db_password = "your-secure-password"

# Monitoring Configuration
alarm_email = "your-email@example.com"
```

**중요**: `github_org`와 `github_repo`를 실제 GitHub 계정 정보로 변경하세요!

예시:
- GitHub URL이 `https://github.com/imdoyeong/lian-date-app`인 경우
  - `github_org = "imdoyeong"`
  - `github_repo = "lian-date-app"`

### 2. Terraform 적용

```bash
cd terraform

# 초기화
terraform init

# 계획 확인
terraform plan

# 적용
terraform apply
```

### 3. GitHub Actions Role ARN 확인

Terraform 적용 후 출력되는 `github_actions_role_arn` 값을 복사하세요:

```bash
terraform output github_actions_role_arn
```

출력 예시:
```
arn:aws:iam::123456789012:role/lian-date-prod-github-actions-role
```

### 4. GitHub Secrets 설정

GitHub 리포지토리 → Settings → Secrets and variables → Actions → New repository secret

다음 두 개의 secret을 추가하세요:

| Secret Name | Value | 설명 |
|-------------|-------|------|
| `AWS_ROLE_ARN` | `arn:aws:iam::xxxx:role/lian-date-prod-github-actions-role` | 3단계에서 확인한 Role ARN |
| `AWS_ACCOUNT_ID` | `123456789012` | AWS 계정 ID (`terraform output account_id`) |

### 5. CI/CD 파이프라인 테스트

#### 방법 1: 코드 Push (자동 트리거)
```bash
git checkout -b feature/test-cicd
git commit --allow-empty -m "test: CI/CD 파이프라인 테스트"
git push origin feature/test-cicd
```

#### 방법 2: 수동 트리거
- GitHub 리포지토리 → Actions → CI/CD Pipeline → Run workflow

### 6. 배포 확인

1. **GitHub Actions 로그 확인**
   - GitHub → Actions 탭에서 워크플로우 실행 상태 확인

2. **AWS ECS 서비스 확인**
   ```bash
   aws ecs describe-services \
     --cluster lian-date-prod-cluster \
     --services lian-date-prod-backend-service lian-date-prod-frontend-service \
     --region us-east-1 \
     --query 'services[*].[serviceName, desiredCount, runningCount]' \
     --output table
   ```

3. **ALB 접속 테스트**
   ```bash
   # ALB DNS 확인
   terraform output alb_dns_name

   # Backend Health Check
   curl http://<alb-dns-name>/health

   # Frontend 접속
   curl http://<alb-dns-name>/
   ```

## 워크플로우 동작 방식

| 브랜치 | Push 시 | Pull Request 시 |
|--------|---------|----------------|
| `main` | 테스트 → 빌드 → ECR Push → ECS 배포 → Smoke Tests | 테스트만 실행 |
| `develop` | 테스트 → 빌드 → ECR Push (배포 안 함) | 테스트만 실행 |
| `feature/**` | 테스트만 실행 | 테스트만 실행 |

## 권한 구조

### GitHub Actions Role 권한

Terraform으로 생성된 IAM Role(`lian-date-prod-github-actions-role`)은 다음 권한을 가집니다:

1. **ECR 권한**
   - `ecr:GetAuthorizationToken` (ECR 로그인)
   - `ecr:BatchGetImage`, `ecr:GetDownloadUrlForLayer` (이미지 다운로드)
   - `ecr:PutImage`, `ecr:InitiateLayerUpload`, `ecr:UploadLayerPart`, `ecr:CompleteLayerUpload` (이미지 업로드)

2. **ECS 권한**
   - `ecs:UpdateService` (서비스 업데이트)
   - `ecs:DescribeServices`, `ecs:DescribeTasks`, `ecs:ListTasks` (상태 확인)

3. **ALB 권한**
   - `elasticloadbalancing:DescribeLoadBalancers` (ALB 정보 조회)
   - `elasticloadbalancing:DescribeTargetGroups`, `DescribeTargetHealth` (Smoke Tests용)

## 트러블슈팅

### 문제 1: "Error assuming role"

**원인**: GitHub OIDC Provider가 없거나 Role trust policy가 잘못됨

**해결**:
```bash
# Terraform 재적용
terraform apply

# github_org, github_repo 변수가 올바른지 확인
terraform output | grep github
```

### 문제 2: "Permission denied to push to ECR"

**원인**: IAM Role에 ECR 권한이 없음

**해결**:
```bash
# IAM Role 정책 확인
aws iam list-attached-role-policies \
  --role-name lian-date-prod-github-actions-role

# Terraform 재적용
terraform apply
```

### 문제 3: "ECS service not found"

**원인**: ECS 서비스가 생성되지 않았거나 이름이 다름

**해결**:
```bash
# ECS 서비스 목록 확인
aws ecs list-services --cluster lian-date-prod-cluster

# 서비스 이름 확인
terraform output backend_service_name
terraform output frontend_service_name
```

### 문제 4: Smoke Tests 실패

**원인**: ALB가 아직 준비되지 않았거나 health check가 실패

**해결**:
```bash
# ALB 상태 확인
aws elbv2 describe-load-balancers \
  --names lian-date-prod-alb \
  --query 'LoadBalancers[0].State' \
  --output text

# Target Group Health 확인
aws elbv2 describe-target-health \
  --target-group-arn $(terraform output -raw backend_target_group_arn)
```

## 보안 고려사항

### 1. OIDC vs Access Keys

✅ **OIDC Provider 사용 (현재 구성)**
- 장기 자격 증명 노출 위험 없음
- 임시 credentials 자동 생성
- GitHub Actions에서만 사용 가능하도록 제한
- AWS 권장 방식

❌ **Access Keys 사용 (사용 안 함)**
- 장기 자격 증명 GitHub Secrets에 저장
- 유출 시 보안 위험
- 주기적인 rotation 필요

### 2. 최소 권한 원칙

현재 IAM Role은 다음 원칙을 따릅니다:
- CI/CD에 필요한 최소한의 권한만 부여
- 읽기 권한은 describe/list로 제한
- 쓰기 권한은 특정 작업(UpdateService, PutImage)으로 제한

### 3. 리포지토리 제한

Trust policy에서 특정 GitHub 리포지토리만 허용:
```json
{
  "StringLike": {
    "token.actions.githubusercontent.com:sub": "repo:your-org/lian-date-app:*"
  }
}
```

## 추가 설정

### 1. Slack 알림 추가

`.github/workflows/cd-prod.yml`에 Slack 알림 step 추가:

```yaml
- name: Notify Slack
  if: always()
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

### 2. Rollback 자동화

배포 실패 시 자동 rollback:

```yaml
- name: Rollback on failure
  if: failure()
  run: |
    aws ecs update-service \
      --cluster lian-date-prod-cluster \
      --service lian-date-prod-backend-service \
      --force-new-deployment \
      --region us-east-1
```

### 3. Blue/Green 배포

ECS Blue/Green 배포를 위한 CodeDeploy 설정 추가 가능.

## 참고 자료

- [GitHub Actions OIDC with AWS](https://docs.github.com/en/actions/deployment/security-hardening-your-deployments/configuring-openid-connect-in-amazon-web-services)
- [AWS ECS Deployment](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/deployment-types.html)
- [ECR User Guide](https://docs.aws.amazon.com/AmazonECR/latest/userguide/what-is-ecr.html)
