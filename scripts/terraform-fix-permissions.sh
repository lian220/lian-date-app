#!/bin/bash

# Terraform IAM 권한 추가 스크립트
# 사용법: ./docs/terraform-fix-permissions.sh

set -e

USER_NAME="lian-terraform-user"

echo "=== Terraform IAM 권한 추가 ==="
echo ""
echo "IAM 사용자: $USER_NAME"
echo ""

# 1. Application AutoScaling 권한 추가
echo "1. Application AutoScaling 권한 추가 중..."
aws iam attach-user-policy \
  --user-name "$USER_NAME" \
  --policy-arn arn:aws:iam::aws:policy/AWSApplicationAutoScalingFullAccess

if [ $? -eq 0 ]; then
  echo "   ✓ Application AutoScaling 권한 추가 완료"
else
  echo "   ✗ Application AutoScaling 권한 추가 실패"
  exit 1
fi

# 2. SNS 권한 추가
echo ""
echo "2. SNS 권한 추가 중..."
aws iam attach-user-policy \
  --user-name "$USER_NAME" \
  --policy-arn arn:aws:iam::aws:policy/AmazonSNSFullAccess

if [ $? -eq 0 ]; then
  echo "   ✓ SNS 권한 추가 완료"
else
  echo "   ✗ SNS 권한 추가 실패"
  exit 1
fi

# 3. 권한 확인
echo ""
echo "3. 현재 연결된 정책 확인:"
aws iam list-attached-user-policies --user-name "$USER_NAME" \
  --query 'AttachedPolicies[*].PolicyName' \
  --output table

echo ""
echo "✅ 권한 추가 완료!"
echo ""
echo "다음 단계:"
echo "  cd terraform"
echo "  terraform apply -var-file=environments/prod/terraform.tfvars"
