output "db_password_secret_arn" {
  description = "ARN of the database password secret"
  value       = aws_secretsmanager_secret.db_password.arn
}

output "kakao_keys_secret_arn" {
  description = "ARN of the Kakao API keys secret"
  value       = aws_secretsmanager_secret.kakao_keys.arn
}

output "openai_key_secret_arn" {
  description = "ARN of the OpenAI API key secret"
  value       = aws_secretsmanager_secret.openai_key.arn
}

output "secrets_policy_arn" {
  description = "ARN of the IAM policy for reading secrets"
  value       = aws_iam_policy.secrets_read.arn
}
