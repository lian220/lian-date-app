# =============================================================================
# SSM Parameter Store Module Outputs
# =============================================================================

# -----------------------------------------------------------------------------
# Parameter ARNs (for ECS Task Definition secrets)
# -----------------------------------------------------------------------------
output "db_host_arn" {
  description = "ARN of the DB host parameter"
  value       = aws_ssm_parameter.db_host.arn
}

output "db_port_arn" {
  description = "ARN of the DB port parameter"
  value       = aws_ssm_parameter.db_port.arn
}

output "db_name_arn" {
  description = "ARN of the DB name parameter"
  value       = aws_ssm_parameter.db_name.arn
}

output "db_username_arn" {
  description = "ARN of the DB username parameter"
  value       = aws_ssm_parameter.db_username.arn
}

output "db_password_arn" {
  description = "ARN of the DB password parameter"
  value       = aws_ssm_parameter.db_password.arn
}

output "openai_api_key_arn" {
  description = "ARN of the OpenAI API key parameter"
  value       = aws_ssm_parameter.openai_api_key.arn
}

output "kakao_rest_api_key_arn" {
  description = "ARN of the Kakao REST API key parameter"
  value       = aws_ssm_parameter.kakao_rest_api_key.arn
}

output "kakao_javascript_key_arn" {
  description = "ARN of the Kakao JavaScript key parameter"
  value       = aws_ssm_parameter.kakao_javascript_key.arn
}

# -----------------------------------------------------------------------------
# Parameter Names (for reference)
# -----------------------------------------------------------------------------
output "parameter_prefix" {
  description = "Prefix for all SSM parameters"
  value       = "/${var.project_name}/${var.environment}"
}

# -----------------------------------------------------------------------------
# All Parameter ARNs (for IAM policy)
# -----------------------------------------------------------------------------
output "all_parameter_arns" {
  description = "List of all SSM parameter ARNs for IAM policy"
  value = [
    aws_ssm_parameter.db_host.arn,
    aws_ssm_parameter.db_port.arn,
    aws_ssm_parameter.db_name.arn,
    aws_ssm_parameter.db_username.arn,
    aws_ssm_parameter.db_password.arn,
    aws_ssm_parameter.openai_api_key.arn,
    aws_ssm_parameter.kakao_rest_api_key.arn,
    aws_ssm_parameter.kakao_javascript_key.arn,
  ]
}
