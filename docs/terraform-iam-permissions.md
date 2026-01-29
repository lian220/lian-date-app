# Terraform 실행을 위한 IAM 권한 가이드

## 문제 상황

Terraform 실행 시 다음 에러가 발생할 수 있습니다:

1. **Application AutoScaling 권한 부족**
   ```
   AccessDeniedException: User is not authorized to perform: 
   application-autoscaling:RegisterScalableTarget
   ```

2. **SNS 권한 부족**
   ```
   AccessDeniedException: User is not authorized to perform: 
   sns:CreateTopic
   ```

## 해결 방법

### 방법 1: IAM 정책 업데이트 (권장)

`lian-terraform-user` IAM 사용자에게 다음 권한을 추가하세요:

#### AWS Management Console에서:

1. **IAM** → **Users** → `lian-terraform-user` 선택
2. **Add permissions** → **Attach policies directly** 클릭
3. 다음 정책들을 추가:

**필수 정책:**
- `AmazonEC2FullAccess` (또는 필요한 EC2 권한만)
- `AmazonECS_FullAccess` (또는 필요한 ECS 권한만)
- `AmazonRDSFullAccess` (또는 필요한 RDS 권한만)
- `ElasticLoadBalancingFullAccess`
- `CloudWatchFullAccess`
- `AmazonSNSFullAccess`
- `ApplicationAutoScalingFullAccess`

**또는 커스텀 정책 생성:**

`terraform/policies/github-actions-policy.json` 파일을 참고하여 필요한 권한만 포함한 정책을 생성하세요.

### 방법 2: 커스텀 정책 사용

`terraform/policies/github-actions-policy.json` 파일이 업데이트되었습니다. 이 파일을 사용하여 정책을 생성하세요:

1. **IAM** → **Policies** → **Create policy**
2. **JSON** 탭 선택
3. `terraform/policies/github-actions-policy.json` 파일 내용 붙여넣기
4. Policy 이름: `TerraformDeploymentPolicy`
5. **Create policy** 클릭
6. `lian-terraform-user`에게 정책 연결

### 방법 3: AWS CLI로 권한 추가

```bash
# 정책 생성
aws iam create-policy \
  --policy-name TerraformDeploymentPolicy \
  --policy-document file://terraform/policies/github-actions-policy.json

# 사용자에게 정책 연결
aws iam attach-user-policy \
  --user-name lian-terraform-user \
  --policy-arn arn:aws:iam::ACCOUNT_ID:policy/TerraformDeploymentPolicy
```

## 필요한 권한 목록

### Application AutoScaling
- `application-autoscaling:RegisterScalableTarget`
- `application-autoscaling:DeregisterScalableTarget`
- `application-autoscaling:DescribeScalableTargets`
- `application-autoscaling:PutScalingPolicy`
- `application-autoscaling:DeleteScalingPolicy`
- `application-autoscaling:DescribeScalingPolicies`

### SNS (Simple Notification Service)
- `sns:CreateTopic`
- `sns:DeleteTopic`
- `sns:GetTopicAttributes`
- `sns:SetTopicAttributes`
- `sns:Subscribe`
- `sns:Unsubscribe`
- `sns:ListSubscriptionsByTopic`
- `sns:Publish`

### 기타 Terraform 실행에 필요한 권한
- EC2 (VPC, Subnet, Security Group 등)
- ECS (Cluster, Service, Task Definition 등)
- ECR (Repository 관리)
- RDS (Database 인스턴스)
- ELB/ALB (Load Balancer)
- CloudWatch (Logs, Alarms)
- IAM (Role 생성 및 관리)

## 권한 확인

권한 추가 후 다음 명령어로 확인:

```bash
# 현재 사용자 확인
aws sts get-caller-identity

# 권한 테스트
aws application-autoscaling describe-scalable-targets \
  --service-namespace ecs

aws sns list-topics
```

## 참고

- 최소 권한 원칙: 프로덕션 환경에서는 필요한 권한만 부여하는 것이 좋습니다.
- 정책 파일 위치: `terraform/policies/github-actions-policy.json`
- 업데이트된 정책에는 Application AutoScaling 및 SNS 권한이 포함되어 있습니다.
