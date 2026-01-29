# Terraform AWS 배포 가이드

LAD-87 브랜치의 Terraform 코드를 사용하여 AWS에 인프라를 배포하는 가이드입니다.

## 📋 전제 조건

1. **AWS 계정 및 인증**
   - AWS CLI 설치 및 구성 완료
   - AWS 자격 증명 설정 (`aws configure` 또는 환경 변수)
   - 필요한 권한: IAM, VPC, EC2, ECS, RDS, ALB, ECR, CloudWatch 등

2. **Terraform 설치**
   ```bash
   # macOS
   brew install terraform
   
   # 또는 직접 다운로드
   # https://www.terraform.io/downloads
   ```

3. **LAD-87 브랜치 체크아웃**
   ```bash
   git checkout feature/LAD-87-aws-account-setup-terraform-backend
   ```

## 🚀 배포 단계

### 1단계: Terraform Backend 설정 (선택사항, 권장)

Terraform state를 안전하게 관리하기 위해 S3 + DynamoDB backend를 설정합니다.

#### 1.1 S3 버킷 생성 (Terraform State 저장용)

```bash
# S3 버킷 생성 (전역적으로 고유한 이름 필요)
aws s3 mb s3://lian-date-terraform-state-$(date +%s) --region us-east-1

# 버킷 이름을 환경 변수로 저장
export TF_STATE_BUCKET="lian-date-terraform-state-xxxxx"
```

#### 1.2 DynamoDB 테이블 생성 (State Lock용)

```bash
aws dynamodb create-table \
  --table-name lian-date-terraform-locks \
  --attribute-definitions AttributeName=LockID,AttributeType=S \
  --key-schema AttributeName=LockID,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --region us-east-1
```

#### 1.3 Backend 설정 파일 생성

`terraform/backend.tf` 파일 생성:

```hcl
terraform {
  backend "s3" {
    bucket         = "lian-date-terraform-state-xxxxx"  # 위에서 생성한 버킷 이름
    key            = "lian-date-app/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "lian-date-terraform-locks"
  }
}
```

**참고**: Backend 설정 없이도 배포 가능하지만, 로컬에 state 파일이 저장됩니다 (팀 협업 시 권장하지 않음).

### 2단계: 환경 변수 및 변수 파일 설정

#### 2.1 프로덕션 환경 변수 설정

`terraform/environments/prod/terraform.tfvars` 파일을 확인하고 필요한 값들을 설정합니다:

```hcl
# 필수 변수들
db_username = "dateclick"  # DB 사용자명
db_password = "YOUR_SECURE_PASSWORD"  # 강력한 비밀번호 사용!
alarm_email = "your-email@example.com"  # CloudWatch 알림 이메일
```

**보안 주의사항**:
- `db_password`는 절대 Git에 커밋하지 마세요!
- 프로덕션에서는 AWS Secrets Manager 사용을 권장합니다.

#### 2.2 로컬 환경 변수 설정 (테스트용)

`terraform/environments/local/terraform.tfvars` 파일은 이미 기본값이 설정되어 있습니다.

### 3단계: Terraform 초기화 및 계획

#### 3.1 Terraform 초기화

```bash
cd terraform
terraform init
```

**Backend 설정이 있는 경우**:
- Terraform이 S3 버킷과 DynamoDB 테이블을 사용하도록 설정됩니다.
- 기존 state가 있으면 자동으로 다운로드됩니다.

#### 3.2 배포 계획 확인

```bash
# 프로덕션 환경
terraform plan -var-file=environments/prod/terraform.tfvars

# 로컬 환경 (테스트용)
terraform plan -var-file=environments/local/terraform.tfvars
```

**확인 사항**:
- 생성될 리소스 목록 확인
- 예상 비용 확인 (특히 RDS, NAT Gateway 등)
- 변수 값이 올바르게 설정되었는지 확인

### 4단계: 인프라 배포

#### 4.1 배포 실행

```bash
# 프로덕션 환경
terraform apply -var-file=environments/prod/terraform.tfvars

# 로컬 환경 (테스트용)
terraform apply -var-file=environments/local/terraform.tfvars
```

**주의사항**:
- 배포에는 약 10-20분이 소요될 수 있습니다 (RDS 인스턴스 생성 시간 포함)
- 비용이 발생합니다 (NAT Gateway, RDS, ALB 등)
- 프로덕션 환경에서는 `-auto-approve` 플래그 사용을 권장하지 않습니다

#### 4.2 배포 확인

배포가 완료되면 출력 값들을 확인합니다:

```bash
terraform output
```

주요 출력 값:
- `alb_dns_name`: ALB DNS 이름 (프론트엔드 접근 URL)
- `backend_repository_url`: Backend ECR Repository URL
- `frontend_repository_url`: Frontend ECR Repository URL
- `db_instance_address`: RDS 엔드포인트

### 5단계: 애플리케이션 배포

인프라 배포가 완료되었지만, ECS 서비스는 아직 Docker 이미지가 없어 실행되지 않을 수 있습니다.

#### 5.1 Docker 이미지 빌드 및 푸시

```bash
# Backend 이미지
cd ../backend
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin $(terraform -chdir=../terraform output -raw backend_repository_url | cut -d'/' -f1)
docker build -t $(terraform -chdir=../terraform output -raw backend_repository_url):latest .
docker push $(terraform -chdir=../terraform output -raw backend_repository_url):latest

# Frontend 이미지
cd ../frontend
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin $(terraform -chdir=../terraform output -raw frontend_repository_url | cut -d'/' -f1)
docker build -t $(terraform -chdir=../terraform output -raw frontend_repository_url):latest .
docker push $(terraform -chdir=../terraform output -raw frontend_repository_url):latest
```

#### 5.2 ECS 서비스 강제 업데이트 (이미지 태그 변경)

ECS 서비스가 새 이미지를 자동으로 가져오도록 강제 업데이트:

```bash
# Backend 서비스 업데이트
aws ecs update-service \
  --cluster $(terraform -chdir=terraform output -raw ecs_cluster_name) \
  --service $(terraform -chdir=terraform output -raw backend_service_name) \
  --force-new-deployment \
  --region us-east-1

# Frontend 서비스 업데이트
aws ecs update-service \
  --cluster $(terraform -chdir=terraform output -raw ecs_cluster_name) \
  --service $(terraform -chdir=terraform output -raw frontend_service_name) \
  --force-new-deployment \
  --region us-east-1
```

## 📊 생성되는 리소스

다음 AWS 리소스들이 생성됩니다:

### 네트워크
- VPC (10.0.0.0/16)
- Public Subnets (2개 AZ)
- Private Subnets (2개 AZ)
- Internet Gateway
- NAT Gateway (프로덕션: 2개, 로컬: 1개)
- Route Tables
- VPC Endpoints (S3, ECR)

### 보안
- Security Groups (ALB, ECS, RDS)
- IAM Roles (ECS Task Execution, Task)

### 컴퓨팅
- ECR Repositories (Backend, Frontend)
- ECS Cluster
- ECS Services (Backend, Frontend)
- Application Load Balancer (ALB)

### 데이터베이스
- RDS PostgreSQL 인스턴스
- DB Subnet Group

### 모니터링
- CloudWatch Log Groups
- CloudWatch Alarms
- SNS Topic (알림용)

## 💰 예상 비용

**프로덕션 환경 (월 예상)**:
- NAT Gateway: ~$32 (2개 × $16)
- RDS (db.t4g.small): ~$15-20
- ALB: ~$16
- ECS Fargate: 사용량에 따라 (약 $30-50)
- 기타 (S3, CloudWatch 등): ~$5-10
- **총 예상**: 약 $100-120/월

**로컬 환경 (월 예상)**:
- NAT Gateway: ~$16 (1개)
- RDS (db.t4g.micro): ~$8-10
- ALB: ~$16
- ECS Fargate: 사용량에 따라 (약 $15-25)
- **총 예상**: 약 $60-80/월

**참고**: 실제 비용은 사용량에 따라 달라질 수 있습니다.

## 🔧 트러블슈팅

### 문제: "Error: No valid credential sources found"

**원인**: AWS 자격 증명이 설정되지 않음

**해결**:
```bash
aws configure
# 또는
export AWS_ACCESS_KEY_ID=xxx
export AWS_SECRET_ACCESS_KEY=xxx
```

### 문제: "Error creating RDS instance: InsufficientDBInstanceCapacity"

**원인**: 선택한 리전에서 해당 인스턴스 클래스 사용 불가

**해결**: 다른 리전 사용 또는 인스턴스 클래스 변경

### 문제: "Error: Error waiting for ECS service to become stable"

**원인**: Docker 이미지가 ECR에 없거나, Task Definition 오류

**해결**:
1. ECR에 이미지가 있는지 확인
2. ECS Task Definition 로그 확인
3. CloudWatch Logs에서 오류 확인

### 문제: Backend 설정 오류

**원인**: S3 버킷 또는 DynamoDB 테이블이 없음

**해결**:
```bash
# Backend 없이 초기화 (로컬 state 사용)
terraform init -backend=false

# 또는 Backend 리소스 먼저 생성
```

## 🗑️ 리소스 삭제

**주의**: 모든 리소스가 삭제되며 데이터는 복구할 수 없습니다!

```bash
# 프로덕션 환경 삭제
terraform destroy -var-file=environments/prod/terraform.tfvars

# 로컬 환경 삭제
terraform destroy -var-file=environments/local/terraform.tfvars
```

## 📚 다음 단계

1. **GitHub Actions CI/CD 설정**
   - `.github/workflows/cd-prod.yml` 확인
   - 자동 배포 파이프라인 구축

2. **모니터링 설정**
   - CloudWatch 대시보드 생성
   - 알림 규칙 조정

3. **보안 강화**
   - Secrets Manager로 민감 정보 관리
   - WAF 설정 (선택사항)
   - SSL/TLS 인증서 설정 (ACM)

4. **비용 최적화**
   - Auto Scaling 정책 조정
   - Reserved Instances 고려 (RDS)
   - CloudWatch 비용 모니터링

---

**작성일**: 2026-01-26
**관련 티켓**: LAD-87
**상태**: 배포 가능
