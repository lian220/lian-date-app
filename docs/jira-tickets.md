# Jira 티켓 생성 가이드

## Terraform AWS 인프라 구축 티켓

### LAD-3: Terraform 초기 설정 및 프로젝트 구조 생성

**Issue Type**: Task
**Priority**: High
**Story Points**: 3
**Labels**: terraform, infrastructure, setup

**Summary**
Terraform 초기 설정 및 프로젝트 구조 생성

**Description**
Terraform 프로젝트 구조를 생성하고 AWS Provider 설정을 완료합니다.

**작업 내용**
- Terraform 디렉토리 구조 생성 (terraform/, modules/, environments/)
- AWS Provider 설정 (리전: ap-northeast-2, Terraform >= 1.0, AWS Provider >= 5.0)
- Backend 설정 (S3 + DynamoDB for state lock)
  - S3 버킷: lian-date-terraform-state
  - DynamoDB 테이블: lian-date-terraform-locks
- 환경별 변수 정의 (local, prod)

**산출물**
- terraform/main.tf
- terraform/variables.tf
- terraform/outputs.tf
- terraform/backend.tf
- terraform/versions.tf
- terraform/environments/local/terraform.tfvars
- terraform/environments/prod/terraform.tfvars

**Acceptance Criteria**
- [ ] terraform init 명령어가 정상 실행됨
- [ ] Backend 설정이 완료되어 State가 S3에 저장됨
- [ ] 환경별 tfvars 파일로 환경 분리가 가능함

---

### LAD-4: VPC 및 네트워크 리소스 구성

**Issue Type**: Task
**Priority**: High
**Story Points**: 5
**Labels**: terraform, infrastructure, networking, vpc
**Blocked By**: LAD-3

**Summary**
VPC 및 네트워크 리소스 구성

**Description**
AWS VPC 및 네트워크 인프라를 Terraform으로 구성합니다. Multi-AZ 구성으로 고가용성을 확보합니다.

**작업 내용**
- VPC 생성 (CIDR: 10.0.0.0/16)
- Public Subnet 2개 (10.0.1.0/24 - 2a, 10.0.2.0/24 - 2c)
- Private Subnet 2개 (10.0.11.0/24 - 2a, 10.0.12.0/24 - 2c)
- Internet Gateway & NAT Gateway
- Route Tables (Public, Private)
- VPC Endpoints (S3, ECR API, ECR DKR)

**산출물**
- terraform/modules/network/vpc.tf
- terraform/modules/network/subnets.tf
- terraform/modules/network/gateways.tf
- terraform/modules/network/routes.tf
- terraform/modules/network/endpoints.tf
- terraform/modules/network/outputs.tf

**Acceptance Criteria**
- [ ] VPC가 정상 생성되고 2개 AZ에 걸쳐 구성됨
- [ ] Public Subnet에서 인터넷 접근 가능
- [ ] Private Subnet에서 NAT Gateway를 통한 아웃바운드 연결 가능
- [ ] VPC Endpoint를 통해 S3, ECR 접근 가능

---

### LAD-5: Security Groups 구성

**Issue Type**: Task
**Priority**: High
**Story Points**: 3
**Labels**: terraform, infrastructure, security
**Blocked By**: LAD-4

**Summary**
Security Groups 구성

**Description**
각 리소스별 보안 그룹을 정의하여 최소 권한 원칙을 적용합니다.

**작업 내용**
- ALB Security Group (Inbound: 80, 443 from 0.0.0.0/0)
- ECS Security Group (Inbound: 8080 from ALB SG only)
- RDS Security Group (Inbound: 5432 from ECS SG only)

**산출물**
- terraform/modules/security/security_groups.tf
- terraform/modules/security/outputs.tf

**Acceptance Criteria**
- [ ] 각 Security Group이 최소 권한으로 구성됨
- [ ] ALB → ECS → RDS 연결이 정상 작동함
- [ ] 불필요한 포트가 열려있지 않음

---

### LAD-6: ECR Repository 생성

**Issue Type**: Task
**Priority**: High
**Story Points**: 2
**Labels**: terraform, infrastructure, ecr, docker
**Blocked By**: LAD-3

**Summary**
ECR Repository 생성

**Description**
Docker 이미지를 저장할 ECR Repository를 생성합니다.

**작업 내용**
- ECR Repository 생성 (lian-date-app-backend)
- Image tag mutability: MUTABLE
- Image scanning: 활성화
- Lifecycle Policy 설정 (최근 10개 이미지 유지, Untagged 1일 후 삭제)
- Repository Policy 설정 (ECS Task Execution Role에 Pull 권한)

**산출물**
- terraform/modules/ecr/repository.tf
- terraform/modules/ecr/lifecycle_policy.tf
- terraform/modules/ecr/outputs.tf

**Acceptance Criteria**
- [ ] ECR Repository가 생성됨
- [ ] Lifecycle Policy가 적용되어 오래된 이미지가 자동 삭제됨
- [ ] ECS에서 이미지 Pull 가능

---

### LAD-7: RDS PostgreSQL 구성

**Issue Type**: Task
**Priority**: High
**Story Points**: 5
**Labels**: terraform, infrastructure, database, rds
**Blocked By**: LAD-4, LAD-5

**Summary**
RDS PostgreSQL 구성

**Description**
PostgreSQL RDS 인스턴스를 생성하고 백업/보안 설정을 완료합니다.

**작업 내용**
- DB Subnet Group 생성 (Private Subnet 2개)
- RDS Instance 생성
  - Engine: PostgreSQL 16
- Instance Class: db.t3.micro (local), db.t3.small (prod)
  - Storage: 20GB (GP3)
  - Multi-AZ: Prod만 활성화
  - Backup retention: 7일
- Parameter Group 설정 (max_connections: 100, Timezone: Asia/Seoul)
- Secrets Manager 연동 (DB 자격증명 저장, 30일 자동 로테이션)

**산출물**
- terraform/modules/rds/db_subnet_group.tf
- terraform/modules/rds/db_instance.tf
- terraform/modules/rds/parameter_group.tf
- terraform/modules/rds/secrets.tf
- terraform/modules/rds/outputs.tf

**Acceptance Criteria**
- [ ] RDS 인스턴스가 Private Subnet에 생성됨
- [ ] ECS에서 RDS 연결 가능
- [ ] 자동 백업이 7일간 유지됨
- [ ] DB 자격증명이 Secrets Manager에 안전하게 저장됨

---

### LAD-8: Application Load Balancer 구성

**Issue Type**: Task
**Priority**: High
**Story Points**: 4
**Labels**: terraform, infrastructure, alb, load-balancer
**Blocked By**: LAD-4, LAD-5

**Summary**
Application Load Balancer 구성

**Description**
Public Subnet에 ALB를 배치하여 트래픽을 ECS로 라우팅합니다.

**작업 내용**
- ALB 생성 (Internet-facing, Public Subnet 2개)
- Target Group 생성 (Type: IP, Protocol: HTTP, Port: 8080)
- Health Check 설정 (/actuator/health, 30초 간격)
- Listener 설정 (HTTP 80 → HTTPS 리다이렉트, HTTPS 443 → Target Group)

**산출물**
- terraform/modules/alb/alb.tf
- terraform/modules/alb/target_group.tf
- terraform/modules/alb/listener.tf
- terraform/modules/alb/outputs.tf

**Acceptance Criteria**
- [ ] ALB가 Public Subnet에 생성됨
- [ ] Health Check가 정상 작동함
- [ ] HTTP → HTTPS 리다이렉트 작동
- [ ] Target Group에 ECS Task 등록 가능

---

### LAD-9: ECS Cluster 및 Fargate Service 구성

**Issue Type**: Task
**Priority**: High
**Story Points**: 8
**Labels**: terraform, infrastructure, ecs, fargate
**Blocked By**: LAD-4, LAD-5, LAD-6, LAD-8

**Summary**
ECS Cluster 및 Fargate Service 구성

**Description**
ECS Cluster와 Fargate Service를 구성하여 컨테이너 애플리케이션을 배포합니다.

**작업 내용**
- ECS Cluster 생성 (lian-date-cluster, Capacity: FARGATE)
- Task Definition 작성
  - Launch Type: FARGATE
  - CPU: 512, Memory: 1024 MB
  - Container: ECR 이미지, Port 8080
  - Environment Variables: SPRING_PROFILES_ACTIVE, DB 정보
  - Secrets: Kakao/OpenAI API Key, DB 자격증명
  - Logging: CloudWatch Logs
- IAM Role 생성 (Task Execution Role, Task Role)
- ECS Service 생성
- Desired Count: 1 (local), 2 (prod)
  - Rolling Update
  - Private Subnet 배치
  - ALB Target Group 연결
  - Auto Scaling (Target CPU 70%, Min 1, Max 4)

**산출물**
- terraform/modules/ecs/cluster.tf
- terraform/modules/ecs/task_definition.tf
- terraform/modules/ecs/iam_roles.tf
- terraform/modules/ecs/service.tf
- terraform/modules/ecs/autoscaling.tf
- terraform/modules/ecs/cloudwatch.tf
- terraform/modules/ecs/outputs.tf

**Acceptance Criteria**
- [ ] ECS Cluster가 생성됨
- [ ] Task Definition이 정상 등록됨
- [ ] ECS Service가 Private Subnet에서 실행됨
- [ ] ALB를 통해 애플리케이션 접근 가능
- [ ] CloudWatch Logs에 로그가 정상 기록됨
- [ ] Auto Scaling이 CPU 기반으로 작동함

---

### LAD-10: CloudWatch Monitoring 및 Alarms 설정

**Issue Type**: Task
**Priority**: Medium
**Story Points**: 3
**Labels**: terraform, infrastructure, monitoring, cloudwatch
**Blocked By**: LAD-9

**Summary**
CloudWatch Monitoring 및 Alarms 설정

**Description**
CloudWatch를 통한 모니터링 및 알람 설정으로 시스템 안정성을 확보합니다.

**작업 내용**
- CloudWatch Log Groups 생성 (/ecs/lian-date-app, /aws/alb/lian-date-alb)
- CloudWatch Alarms 생성
  - ECS: CPU/Memory > 80%, Running Task < Desired
  - ALB: Unhealthy Target > 0, 5xx Error Rate > 5%
  - RDS: CPU > 80%, Storage < 20%, Connections > 80
- SNS Topic 생성 (이메일 알림)

**산출물**
- terraform/modules/monitoring/log_groups.tf
- terraform/modules/monitoring/alarms.tf
- terraform/modules/monitoring/sns.tf
- terraform/modules/monitoring/outputs.tf

**Acceptance Criteria**
- [ ] CloudWatch Logs에 애플리케이션 로그가 기록됨
- [ ] 설정한 임계값 초과 시 알람 발생
- [ ] SNS를 통해 이메일 알림 수신

---

## GitHub Actions CI/CD 파이프라인 티켓

### LAD-11: GitHub Actions 워크플로우 초기 설정

**Issue Type**: Task
**Priority**: High
**Story Points**: 3
**Labels**: cicd, github-actions, devops
**Blocked By**: LAD-6, LAD-9

**Summary**
GitHub Actions 워크플로우 초기 설정

**Description**
GitHub Actions를 이용한 CI/CD 파이프라인의 기본 구조를 설정합니다.

**작업 내용**
- 워크플로우 파일 생성 (.github/workflows/ci.yml, cd-local.yml, cd-prod.yml)
- GitHub Secrets 설정 (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, ECR_REPOSITORY 등)
- Composite Actions 생성 (build-and-test, docker-build-push)

**산출물**
- .github/workflows/ci.yml
- .github/workflows/cd-local.yml
- .github/workflows/cd-prod.yml
- .github/actions/build-and-test/action.yml
- .github/actions/docker-build-push/action.yml

**Acceptance Criteria**
- [ ] GitHub Secrets가 모두 등록됨
- [ ] 워크플로우 파일이 정상적으로 파싱됨
- [ ] Composite Actions가 재사용 가능하게 구성됨

---

### LAD-12: CI 파이프라인 구현 (테스트 및 빌드)

**Issue Type**: Task
**Priority**: High
**Story Points**: 5
**Labels**: cicd, github-actions, testing
**Blocked By**: LAD-11

**Summary**
CI 파이프라인 구현 (테스트 및 빌드)

**Description**
Pull Request 생성 시 자동으로 테스트 및 빌드를 수행합니다.

**작업 내용**
- Trigger: pull_request (branches: main, develop)
- Job 1: Test
  - Java 21 설정
  - Gradle 캐시
  - 단위/통합 테스트 실행
  - JaCoCo 커버리지 리포트 (80% 이상)
- Job 2: Build
  - Gradle bootJar 빌드
  - Docker 이미지 빌드 (태그: PR 번호)
  - Trivy 보안 스캔
- Status Check 설정 (PR 머지 전 CI 통과 필수)

**산출물**
- .github/workflows/ci.yml (완성)
- .github/actions/build-and-test/action.yml (완성)

**Acceptance Criteria**
- [ ] PR 생성 시 CI 워크플로우 자동 실행
- [ ] 테스트 실패 시 워크플로우 실패
- [ ] 빌드 성공 시 Docker 이미지 생성
- [ ] 보안 스캔 결과가 PR 코멘트로 표시됨

---

### LAD-13: CD 파이프라인 구현 - Local 환경

**Issue Type**: Task
**Priority**: High
**Story Points**: 5
**Labels**: cicd, github-actions, deployment
**Blocked By**: LAD-11, LAD-12

**Summary**
CD 파이프라인 구현 - Local 환경

**Description**
develop 브랜치에 머지 시 자동으로 Local 환경에 배포합니다.

**작업 내용**
- Trigger: push (branch: develop)
- Job 1: Build and Push
  - ECR 로그인
- Docker 이미지 빌드 (태그: local-{commit_sha}, local-latest)
  - ECR에 이미지 Push
- Job 2: Deploy to ECS
  - 새로운 Task Definition 등록
  - ECS Service 업데이트 (Force new deployment)
  - Wait for stability (10분)
- Rollback 전략 (배포 실패 시 자동 롤백)

**산출물**
- .github/workflows/cd-local.yml (완성)
- .github/actions/docker-build-push/action.yml (완성)

**Acceptance Criteria**
- [ ] develop 브랜치 머지 시 자동 배포
- [ ] ECR에 이미지가 정상 Push됨
- [ ] ECS Service가 새로운 Task Definition으로 업데이트됨
- [ ] Health Check 통과 후 배포 완료
- [ ] 배포 실패 시 자동 롤백

---

### LAD-14: CD 파이프라인 구현 - Prod 환경

**Issue Type**: Task
**Priority**: High
**Story Points**: 5
**Labels**: cicd, github-actions, deployment, production
**Blocked By**: LAD-13

**Summary**
CD 파이프라인 구현 - Prod 환경

**Description**
main 브랜치에 머지 시 수동 승인 후 Prod 환경에 배포합니다.

**작업 내용**
- Trigger: push (branch: main)
- Job 1: Build and Push
  - Git 태그 생성 (SemVer: v1.0.0)
  - Docker 이미지 빌드 (태그: prod-{version}, prod-latest)
  - ECR에 이미지 Push
- Job 2: Manual Approval (Environment: production, Required reviewers: 2)
- Job 3: Deploy to ECS
  - Task Definition 등록
  - ECS Service 업데이트
  - Wait for stability (15분)
- Release Notes 자동 생성 (GitHub Release, CHANGELOG.md)

**산출물**
- .github/workflows/cd-prod.yml (완성)
- scripts/generate-release-notes.sh

**Acceptance Criteria**
- [ ] main 브랜치 머지 시 빌드 및 ECR Push
- [ ] Manual Approval 단계에서 대기
- [ ] 승인 후 Prod 환경에 배포
- [ ] GitHub Release 자동 생성
- [ ] 배포 완료 후 슬랙 알림

---

### LAD-15: 배포 모니터링 및 알림 설정

**Issue Type**: Task
**Priority**: Medium
**Story Points**: 3
**Labels**: cicd, monitoring, slack
**Blocked By**: LAD-13, LAD-14

**Summary**
배포 모니터링 및 알림 설정

**Description**
배포 상태를 실시간으로 모니터링하고 알림을 받습니다.

**작업 내용**
- GitHub Actions Status Badge 추가 (README.md)
- Slack 알림 설정 (#deployments 채널)
  - 배포 시작/성공/실패
  - Slack Incoming Webhook 사용
- CloudWatch Dashboard 생성 (배포 빈도, 성공률, 소요 시간)

**산출물**
- .github/workflows/notify-slack.yml
- README.md (배지 추가)
- CloudWatch Dashboard JSON

**Acceptance Criteria**
- [ ] GitHub Actions 상태 배지가 README에 표시됨
- [ ] 배포 이벤트 시 Slack 알림 수신
- [ ] CloudWatch Dashboard에서 배포 히스토리 확인 가능

---

### LAD-16: Dockerfile 최적화 및 Multi-stage Build

**Issue Type**: Task
**Priority**: Medium
**Story Points**: 3
**Labels**: docker, optimization
**Blocked By**: LAD-12

**Summary**
Dockerfile 최적화 및 Multi-stage Build

**Description**
Docker 이미지 크기를 최적화하고 빌드 시간을 단축합니다.

**작업 내용**
- Multi-stage Build 구현 (Builder stage, Runtime stage)
- Alpine Linux 사용 (경량화)
- Layer 캐싱 최적화
- .dockerignore 파일 작성
- Health Check 추가
- Non-root 사용자로 실행 (보안 강화)

**산출물**
- backend/Dockerfile (최적화)
- backend/.dockerignore

**Acceptance Criteria**
- [ ] 이미지 크기가 300MB 이하
- [ ] 빌드 시간이 5분 이내
- [ ] Health Check가 정상 작동
- [ ] 보안 스캔에서 Critical 취약점 없음

---

### LAD-17: 환경별 설정 관리 및 Secrets 통합

**Issue Type**: Task
**Priority**: High
**Story Points**: 4
**Labels**: configuration, security, secrets
**Blocked By**: LAD-7, LAD-13, LAD-14

**Summary**
환경별 설정 관리 및 Secrets 통합

**Description**
환경별 설정을 안전하게 관리하고 Secrets Manager와 통합합니다.

**작업 내용**
- Spring Boot 프로파일 설정 (application-local.yml, application-prod.yml)
- AWS Secrets Manager 연동 (Spring Cloud AWS Secrets Manager)
- Secrets 항목 정의 (/lian-date/local/db, kakao, openai 등)
- ECS Task Definition에서 Secrets 참조
- 로컬 개발 환경 설정 (.env.example)

**산출물**
- backend/src/main/resources/application-local.yml
- backend/src/main/resources/application-prod.yml
- backend/.env.example
- Terraform Secrets Manager 리소스

**Acceptance Criteria**
- [ ] 환경별로 다른 설정이 적용됨
- [ ] Secrets Manager에서 민감 정보 자동 주입
- [ ] 로컬 개발 시 .env 파일로 설정 가능
- [ ] Git에 민감 정보가 커밋되지 않음

---

## 티켓 생성 순서

### Phase 1: 인프라 기반 (Week 1-2)
1. LAD-3: Terraform 초기 설정
2. LAD-4: VPC 및 네트워크
3. LAD-5: Security Groups
4. LAD-6: ECR Repository

### Phase 2: 데이터베이스 및 컴퓨팅 (Week 2-3)
5. LAD-7: RDS PostgreSQL
6. LAD-8: Application Load Balancer
7. LAD-9: ECS Cluster 및 Fargate Service
8. LAD-10: CloudWatch Monitoring

### Phase 3: CI/CD 파이프라인 (Week 3-4)
9. LAD-11: GitHub Actions 초기 설정
10. LAD-12: CI 파이프라인
11. LAD-13: CD Local 환경
12. LAD-14: CD Prod 환경

### Phase 4: 최적화 및 운영 (Week 4)
13. LAD-15: 배포 모니터링 및 알림
14. LAD-16: Dockerfile 최적화
15. LAD-17: 환경별 설정 관리

**총 예상 공수**: 61 Story Points (약 4주)
