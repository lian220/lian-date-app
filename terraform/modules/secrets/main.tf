# Database Password Secret
resource "aws_secretsmanager_secret" "db_password" {
  name        = "${var.name_prefix}-db-password"
  description = "RDS PostgreSQL master password"

  recovery_window_in_days = 7

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-db-password"
    }
  )
}

resource "aws_secretsmanager_secret_version" "db_password" {
  secret_id     = aws_secretsmanager_secret.db_password.id
  secret_string = var.db_password
}

# Kakao API Keys Secret
resource "aws_secretsmanager_secret" "kakao_keys" {
  name        = "${var.name_prefix}-kakao-keys"
  description = "Kakao API keys for map integration"

  recovery_window_in_days = 7

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-kakao-keys"
    }
  )
}

resource "aws_secretsmanager_secret_version" "kakao_keys" {
  secret_id = aws_secretsmanager_secret.kakao_keys.id
  secret_string = jsonencode({
    rest_api_key   = var.kakao_rest_api_key
    javascript_key = var.kakao_javascript_key
  })
}

# OpenAI API Key Secret
resource "aws_secretsmanager_secret" "openai_key" {
  name        = "${var.name_prefix}-openai-key"
  description = "OpenAI API key for AI features"

  recovery_window_in_days = 7

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-openai-key"
    }
  )
}

resource "aws_secretsmanager_secret_version" "openai_key" {
  secret_id     = aws_secretsmanager_secret.openai_key.id
  secret_string = var.openai_api_key
}

# IAM Policy for ECS Tasks to read secrets
data "aws_iam_policy_document" "secrets_read" {
  statement {
    sid    = "ReadSecrets"
    effect = "Allow"

    actions = [
      "secretsmanager:GetSecretValue",
      "secretsmanager:DescribeSecret",
    ]

    resources = [
      aws_secretsmanager_secret.db_password.arn,
      aws_secretsmanager_secret.kakao_keys.arn,
      aws_secretsmanager_secret.openai_key.arn,
    ]
  }

  statement {
    sid    = "DecryptSecrets"
    effect = "Allow"

    actions = [
      "kms:Decrypt",
      "kms:DescribeKey",
    ]

    resources = ["*"]

    condition {
      test     = "StringEquals"
      variable = "kms:ViaService"
      values   = ["secretsmanager.${var.aws_region}.amazonaws.com"]
    }
  }
}

resource "aws_iam_policy" "secrets_read" {
  name        = "${var.name_prefix}-secrets-read"
  description = "Allow ECS tasks to read application secrets"
  policy      = data.aws_iam_policy_document.secrets_read.json

  tags = var.tags
}
