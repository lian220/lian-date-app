#!/bin/bash

# GitHub Actions용 AWS OIDC Provider 및 IAM Role 설정 스크립트
# LAD-87: AWS 계정 초기 설정

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 설정 변수
AWS_REGION="${AWS_REGION:-us-east-1}"
GITHUB_ORG="${GITHUB_ORG}"
GITHUB_REPO="${GITHUB_REPO}"
ROLE_NAME="github-actions-deployment-role"
POLICY_NAME="GitHubActionsDeploymentPolicy"
OIDC_PROVIDER_URL="https://token.actions.githubusercontent.com"
OIDC_THUMBPRINT="6938fd4d98bab03faadb97b34396831e3780aea1"

echo -e "${BLUE}======================================${NC}"
echo -e "${BLUE}GitHub Actions AWS OIDC 설정 시작${NC}"
echo -e "${BLUE}======================================${NC}"

# 환경 변수 확인
if [ -z "$GITHUB_ORG" ] || [ -z "$GITHUB_REPO" ]; then
    echo -e "${RED}[ERROR] 필수 환경 변수가 설정되지 않았습니다.${NC}"
    echo ""
    echo "사용법:"
    echo "  GITHUB_ORG=your-org GITHUB_REPO=your-repo ./scripts/setup-aws-oidc.sh"
    echo ""
    echo "예시:"
    echo "  GITHUB_ORG=lian220 GITHUB_REPO=lian-date-app ./scripts/setup-aws-oidc.sh"
    exit 1
fi

# AWS CLI 설치 확인
if ! command -v aws &> /dev/null; then
    echo -e "${RED}[ERROR] AWS CLI가 설치되어 있지 않습니다.${NC}"
    echo "설치 방법: https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html"
    exit 1
fi

# AWS 인증 확인
echo -e "${YELLOW}[INFO] AWS 자격 증명 확인 중...${NC}"
if ! aws sts get-caller-identity &> /dev/null; then
    echo -e "${RED}[ERROR] AWS 자격 증명이 설정되지 않았습니다.${NC}"
    echo "다음 명령어로 설정하세요: aws configure"
    exit 1
fi

AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo -e "${GREEN}[OK] AWS Account ID: ${AWS_ACCOUNT_ID}${NC}"
echo -e "${YELLOW}[INFO] Region: ${AWS_REGION}${NC}"
echo -e "${YELLOW}[INFO] GitHub Repository: ${GITHUB_ORG}/${GITHUB_REPO}${NC}"
echo ""

# 1. OIDC Provider 생성
echo -e "${YELLOW}[STEP 1/3] OIDC Provider 확인 및 생성...${NC}"

OIDC_PROVIDER_ARN="arn:aws:iam::${AWS_ACCOUNT_ID}:oidc-provider/token.actions.githubusercontent.com"

if aws iam get-open-id-connect-provider --open-id-connect-provider-arn "${OIDC_PROVIDER_ARN}" &> /dev/null; then
    echo -e "${GREEN}[OK] OIDC Provider가 이미 존재합니다.${NC}"
else
    echo -e "${YELLOW}[INFO] OIDC Provider 생성 중...${NC}"
    aws iam create-open-id-connect-provider \
        --url "${OIDC_PROVIDER_URL}" \
        --client-id-list "sts.amazonaws.com" \
        --thumbprint-list "${OIDC_THUMBPRINT}"

    echo -e "${GREEN}[OK] OIDC Provider 생성 완료${NC}"
fi

echo -e "${BLUE}OIDC Provider ARN: ${OIDC_PROVIDER_ARN}${NC}"
echo ""

# 2. IAM Policy 생성
echo -e "${YELLOW}[STEP 2/3] IAM Policy 생성...${NC}"

POLICY_ARN="arn:aws:iam::${AWS_ACCOUNT_ID}:policy/${POLICY_NAME}"

if aws iam get-policy --policy-arn "${POLICY_ARN}" &> /dev/null; then
    echo -e "${GREEN}[OK] Policy '${POLICY_NAME}'가 이미 존재합니다.${NC}"
else
    echo -e "${YELLOW}[INFO] Policy '${POLICY_NAME}' 생성 중...${NC}"

    POLICY_JSON_PATH="terraform/policies/github-actions-policy.json"

    if [ ! -f "${POLICY_JSON_PATH}" ]; then
        echo -e "${RED}[ERROR] Policy 파일을 찾을 수 없습니다: ${POLICY_JSON_PATH}${NC}"
        exit 1
    fi

    aws iam create-policy \
        --policy-name "${POLICY_NAME}" \
        --policy-document "file://${POLICY_JSON_PATH}" \
        --description "Policy for GitHub Actions to deploy to ECR and ECS"

    echo -e "${GREEN}[OK] Policy 생성 완료${NC}"
fi

echo -e "${BLUE}Policy ARN: ${POLICY_ARN}${NC}"
echo ""

# 3. IAM Role 생성
echo -e "${YELLOW}[STEP 3/3] IAM Role 생성...${NC}"

ROLE_ARN="arn:aws:iam::${AWS_ACCOUNT_ID}:role/${ROLE_NAME}"

if aws iam get-role --role-name "${ROLE_NAME}" &> /dev/null; then
    echo -e "${YELLOW}[INFO] Role '${ROLE_NAME}'가 이미 존재합니다.${NC}"
    echo -e "${YELLOW}[INFO] Trust Relationship를 업데이트합니다...${NC}"
else
    echo -e "${YELLOW}[INFO] Role '${ROLE_NAME}' 생성 중...${NC}"
fi

# Trust Policy 생성
TRUST_POLICY=$(cat <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "${OIDC_PROVIDER_ARN}"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com"
        },
        "StringLike": {
          "token.actions.githubusercontent.com:sub": "repo:${GITHUB_ORG}/${GITHUB_REPO}:*"
        }
      }
    }
  ]
}
EOF
)

# Role이 존재하는지 확인
if aws iam get-role --role-name "${ROLE_NAME}" &> /dev/null; then
    # Trust Relationship 업데이트
    echo "$TRUST_POLICY" > /tmp/trust-policy.json
    aws iam update-assume-role-policy \
        --role-name "${ROLE_NAME}" \
        --policy-document "file:///tmp/trust-policy.json"
    rm /tmp/trust-policy.json
    echo -e "${GREEN}[OK] Trust Relationship 업데이트 완료${NC}"
else
    # 새 Role 생성
    echo "$TRUST_POLICY" > /tmp/trust-policy.json
    aws iam create-role \
        --role-name "${ROLE_NAME}" \
        --assume-role-policy-document "file:///tmp/trust-policy.json" \
        --description "Role for GitHub Actions to deploy to AWS"
    rm /tmp/trust-policy.json
    echo -e "${GREEN}[OK] Role 생성 완료${NC}"
fi

# Policy를 Role에 연결
echo -e "${YELLOW}[INFO] Policy를 Role에 연결 중...${NC}"
aws iam attach-role-policy \
    --role-name "${ROLE_NAME}" \
    --policy-arn "${POLICY_ARN}" 2>/dev/null || echo -e "${YELLOW}[INFO] Policy가 이미 연결되어 있습니다.${NC}"

echo -e "${GREEN}[OK] Policy 연결 완료${NC}"
echo -e "${BLUE}Role ARN: ${ROLE_ARN}${NC}"
echo ""

# 결과 요약
echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}설정 완료!${NC}"
echo -e "${GREEN}======================================${NC}"
echo ""
echo -e "${YELLOW}다음 정보를 GitHub Secrets에 등록하세요:${NC}"
echo ""
echo -e "${BLUE}AWS_ACCOUNT_ID${NC}     = ${AWS_ACCOUNT_ID}"
echo -e "${BLUE}AWS_REGION${NC}         = ${AWS_REGION}"
echo -e "${BLUE}AWS_ROLE_ARN${NC}       = ${ROLE_ARN}"
echo ""
echo -e "${YELLOW}GitHub Secrets 등록 방법:${NC}"
echo "1. GitHub 리포지토리로 이동: https://github.com/${GITHUB_ORG}/${GITHUB_REPO}"
echo "2. Settings → Secrets and variables → Actions"
echo "3. 'New repository secret' 클릭하여 위 값들을 등록"
echo ""
echo -e "${YELLOW}검증:${NC}"
echo "aws iam get-role --role-name ${ROLE_NAME}"
echo ""
echo -e "${GREEN}설정이 완료되었습니다!${NC}"
