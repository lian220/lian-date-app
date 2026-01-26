# Account Information
output "account_id" {
  description = "AWS Account ID"
  value       = local.account_id
}

output "region" {
  description = "AWS Region"
  value       = var.aws_region
}

output "environment" {
  description = "Environment name"
  value       = var.environment
}

# Resource Naming
output "name_prefix" {
  description = "Common name prefix for resources"
  value       = local.name_prefix
}
