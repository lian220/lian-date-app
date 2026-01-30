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
  value       = "prod"
}

# Resource Naming
output "name_prefix" {
  description = "Common name prefix for resources"
  value       = local.name_prefix
}

# Network Outputs
output "vpc_id" {
  description = "VPC ID"
  value       = module.network.vpc_id
}

output "public_subnet_ids" {
  description = "List of public subnet IDs"
  value       = module.network.public_subnet_ids
}

output "private_subnet_ids" {
  description = "List of private subnet IDs"
  value       = module.network.private_subnet_ids
}

output "nat_gateway_eips" {
  description = "NAT Gateway public IPs"
  value       = module.network.nat_gateway_eips
}

# Security Group Outputs
output "alb_security_group_id" {
  description = "ALB security group ID"
  value       = module.security.alb_security_group_id
}

output "ecs_security_group_id" {
  description = "ECS security group ID"
  value       = module.security.ecs_security_group_id
}

# RDS Security Group - Disabled (Using Supabase)
# output "rds_security_group_id" {
#   description = "RDS security group ID"
#   value       = module.security.rds_security_group_id
# }

# ECR Outputs
output "backend_repository_url" {
  description = "Backend ECR repository URL"
  value       = module.ecr.backend_repository_url
}

output "frontend_repository_url" {
  description = "Frontend ECR repository URL"
  value       = module.ecr.frontend_repository_url
}

# RDS Outputs - Disabled (Using Supabase)
# output "db_instance_endpoint" {
#   description = "RDS instance endpoint"
#   value       = module.rds.db_instance_endpoint
# }
#
# output "db_instance_address" {
#   description = "RDS instance address"
#   value       = module.rds.db_instance_address
# }
#
# output "db_name" {
#   description = "Database name"
#   value       = module.rds.db_name
# }

# Database Configuration
output "db_host" {
  description = "Database host (Supabase)"
  value       = var.db_host
  sensitive   = true
}

output "db_name" {
  description = "Database name"
  value       = var.db_name
}

# ALB Outputs
output "alb_dns_name" {
  description = "ALB DNS name"
  value       = module.alb.alb_dns_name
}

output "backend_target_group_arn" {
  description = "Backend target group ARN"
  value       = module.alb.backend_target_group_arn
}

output "frontend_target_group_arn" {
  description = "Frontend target group ARN"
  value       = module.alb.frontend_target_group_arn
}

# ECS Outputs
output "ecs_cluster_name" {
  description = "ECS cluster name"
  value       = module.ecs.cluster_name
}

output "backend_service_name" {
  description = "Backend ECS service name"
  value       = module.ecs.backend_service_name
}

output "frontend_service_name" {
  description = "Frontend ECS service name"
  value       = module.ecs.frontend_service_name
}

output "backend_log_group_name" {
  description = "Backend CloudWatch log group name"
  value       = module.ecs.backend_log_group_name
}

output "frontend_log_group_name" {
  description = "Frontend CloudWatch log group name"
  value       = module.ecs.frontend_log_group_name
}

# Monitoring Outputs
output "sns_topic_arn" {
  description = "SNS topic ARN for alarms"
  value       = module.monitoring.sns_topic_arn
}

output "alarm_email" {
  description = "Email address receiving alarm notifications"
  value       = var.alarm_email
  sensitive   = true
}

# GitHub OIDC Outputs
output "github_actions_role_arn" {
  description = "ARN of the GitHub Actions IAM role (use as AWS_ROLE_ARN secret)"
  value       = module.github_oidc.github_actions_role_arn
}

output "github_oidc_provider_arn" {
  description = "ARN of the GitHub OIDC provider"
  value       = module.github_oidc.oidc_provider_arn
}

# Secrets Manager Outputs (disabled)
# output "db_password_secret_arn" {
#   description = "ARN of the database password secret"
#   value       = module.secrets.db_password_secret_arn
# }
#
# output "kakao_keys_secret_arn" {
#   description = "ARN of the Kakao API keys secret"
#   value       = module.secrets.kakao_keys_secret_arn
# }
#
# output "openai_key_secret_arn" {
#   description = "ARN of the OpenAI API key secret"
#   value       = module.secrets.openai_key_secret_arn
# }

# Feature Flags
output "https_enabled" {
  description = "Whether HTTPS is enabled"
  value       = var.enable_https
}

output "secrets_manager_enabled" {
  description = "Whether Secrets Manager is enabled"
  value       = var.use_secrets_manager
}

# CodeDeploy Outputs (Blue/Green Deployment)
output "codedeploy_backend_app_name" {
  description = "CodeDeploy application name for backend"
  value       = module.codedeploy.backend_app_name
}

output "codedeploy_frontend_app_name" {
  description = "CodeDeploy application name for frontend"
  value       = module.codedeploy.frontend_app_name
}

output "codedeploy_backend_deployment_group" {
  description = "CodeDeploy deployment group name for backend"
  value       = module.codedeploy.backend_deployment_group_name
}

output "codedeploy_frontend_deployment_group" {
  description = "CodeDeploy deployment group name for frontend"
  value       = module.codedeploy.frontend_deployment_group_name
}
