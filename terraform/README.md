# Terraform Infrastructure

Date Click 프로젝트의 AWS 인프라를 관리하는 Terraform 구성입니다.

## 폴더 구조

```
terraform/
├── main.tf                    # 모듈 조합 및 Provider 설정
├── variables.tf               # 변수 정의
├── outputs.tf                 # 출력 정의
├── versions.tf                # Terraform/Provider 버전
├── terraform.tfvars.example   # 변수 예제 파일
├── .gitignore                 # Git 제외 파일 목록
├── load-env.sh                # 환경변수 로드 스크립트
├── modules/                   # Terraform 모듈
│   ├── network/               # VPC, Subnet, NAT Gateway
│   ├── security/              # Security Groups
│   ├── ecr/                   # Container Registry
│   ├── alb/                   # Application Load Balancer
│   ├── ecs/                   # ECS Fargate
│   ├── ssm/                   # SSM Parameter Store
│   ├── monitoring/            # CloudWatch, SNS
│   ├── codedeploy/            # Blue/Green 배포
│   ├── github-oidc/           # GitHub Actions OIDC (비활성화)
│   ├── secrets/               # Secrets Manager (비활성화)
│   └── rds/                   # RDS (비활성화, Supabase 사용)
└── policies/                  # IAM 정책 참조 문서
    └── github-actions-policy.json
```

## 아키텍처 개요

```
┌─────────────── VPC (10.0.0.0/16) ───────────────┐
│                                                  │
│  ┌──── Public Subnet (Multi-AZ) ────┐           │
│  │  - Application Load Balancer     │           │
│  │  - NAT Gateway (HA)              │           │
│  └────────────────┬─────────────────┘           │
│                   │                              │
│  ┌────────────────▼─────────────────┐           │
│  │  Private Subnet (Multi-AZ)       │           │
│  │  - ECS Tasks (Backend/Frontend)  │           │
│  └──────────────────────────────────┘           │
│                                                  │
└──────────────────────────────────────────────────┘
                    │
                    ▼
            ┌───────────────┐
            │   Supabase    │
            │  (PostgreSQL) │
            └───────────────┘
```

**현재 활성화된 모듈:**
- `network` - VPC, Subnet, NAT Gateway
- `security` - Security Groups
- `ecr` - ECR 레지스트리 (backend, frontend)
- `alb` - Application Load Balancer
- `ecs` - ECS Fargate 클러스터 및 서비스
- `ssm` - SSM Parameter Store (민감 정보 저장)
- `monitoring` - CloudWatch 알람, SNS
- `codedeploy` - Blue/Green 배포

**비활성화된 모듈:**
- `rds` - AWS RDS 대신 Supabase 사용
- `secrets` - Secrets Manager 대신 SSM 사용
- `github-oidc` - 기존 OIDC Provider 사용

---

## Quick Start

### 1. 사전 준비

```bash
# AWS CLI 설정 확인
aws sts get-caller-identity

# Terraform 버전 확인 (v1.0 이상)
terraform version
```

### 2. 변수 설정

```bash
cd terraform
cp terraform.tfvars.example terraform.tfvars
# terraform.tfvars 파일 편집
```

**필수 변수:**

| 변수 | 설명 | 예시 |
|------|------|------|
| `github_org` | GitHub 사용자명/조직명 | `your-username` |
| `github_repo` | GitHub 리포지토리명 | `lian-date-app` |
| `db_host` | Supabase DB 호스트 | `xxx.supabase.com` |
| `db_password` | Supabase DB 비밀번호 | - |
| `openai_api_key` | OpenAI API 키 | `sk-...` |
| `kakao_rest_api_key` | Kakao REST API 키 | - |
| `alarm_email` | 알람 수신 이메일 | `ops@example.com` |

### 3. 배포

**방법 A: 스크립트 사용 (권장)**

```bash
# 프로젝트 루트에서 실행
./terraform-apply.sh apply
```

**방법 B: 수동 실행**

```bash
cd terraform
terraform init
terraform plan
terraform apply
```

### 4. 배포 확인

```bash
# ALB DNS 확인
terraform output alb_dns_name

# Health check
curl http://$(terraform output -raw alb_dns_name)/health
```

---

## 주요 Outputs

```bash
# 모든 출력값 확인
terraform output

# 주요 값들
terraform output alb_dns_name              # ALB 접속 주소
terraform output backend_repository_url    # Backend ECR URL
terraform output frontend_repository_url   # Frontend ECR URL
terraform output ecs_cluster_name          # ECS 클러스터명
terraform output github_actions_role_arn   # GitHub Actions IAM Role ARN
```

---

## CI/CD 설정 (GitHub Actions)

### 1. Terraform 적용 후 Role ARN 확인

```bash
terraform output github_actions_role_arn
# 출력: arn:aws:iam::123456789012:role/lian-date-prod-github-actions-role
```

### 2. GitHub Secrets 설정

GitHub → Settings → Secrets and variables → Actions에 추가:

| Secret Name | Value |
|-------------|-------|
| `AWS_ROLE_ARN` | Terraform output의 `github_actions_role_arn` |
| `AWS_ACCOUNT_ID` | AWS 계정 ID |

### 3. 워크플로우 동작

| 브랜치 | Push | PR |
|--------|------|-----|
| `main` | 빌드 → ECR Push → ECS 배포 | 테스트만 |
| `feature/**` | 테스트만 | 테스트만 |

---

## 비용

| 리소스 | 월 비용 |
|--------|---------|
| Fargate (Backend) | ~$20 |
| Fargate (Frontend) | ~$10 |
| ALB | ~$20 |
| NAT Gateway (2 AZ) | ~$64 |
| CloudWatch | ~$10 |
| **총계** | **~$124/월** |

> RDS는 Supabase로 대체되어 AWS 비용에서 제외됨

---

## 인프라 삭제

```bash
# 스크립트 사용
./terraform-destroy.sh

# 또는 수동
cd terraform
terraform destroy
```

---

## 유틸리티

### load-env.sh

`.env.prod` 파일을 읽어 `TF_VAR_*` 환경변수로 변환:

```bash
source load-env.sh .env.prod
terraform apply
```

### policies/

IAM 정책 참조 문서. Terraform에서 직접 사용되지 않음.

---

## 트러블슈팅

### "Error assuming role"

GitHub OIDC 설정 확인:
```bash
terraform output | grep github
```

### "Permission denied to push to ECR"

IAM Role 정책 확인:
```bash
aws iam list-attached-role-policies \
  --role-name lian-date-prod-github-actions-role
```

### ECS 서비스 상태 확인

```bash
aws ecs describe-services \
  --cluster lian-date-prod-cluster \
  --services lian-date-prod-backend-service lian-date-prod-frontend-service \
  --query 'services[*].[serviceName,runningCount,desiredCount]' \
  --output table
```

---

## 참고 자료

- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [GitHub Actions OIDC with AWS](https://docs.github.com/en/actions/deployment/security-hardening-your-deployments/configuring-openid-connect-in-amazon-web-services)
- [AWS ECS Deployment](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/deployment-types.html)
