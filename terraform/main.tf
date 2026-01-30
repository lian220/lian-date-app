# AWS Provider Configuration
provider "aws" {
  region = var.aws_region

  default_tags {
    tags = merge(
      var.common_tags,
      {
        Environment = "prod"
      }
    )
  }
}

# Data Sources
data "aws_caller_identity" "current" {}

data "aws_availability_zones" "available" {
  state = "available"
}

# Local Variables
locals {
  account_id  = data.aws_caller_identity.current.account_id
  name_prefix = "${var.project_name}-prod"

  az_names = slice(
    data.aws_availability_zones.available.names,
    0,
    length(var.availability_zones)
  )
}

# Network Module
module "network" {
  source = "./modules/network"

  name_prefix        = local.name_prefix
  vpc_cidr           = var.vpc_cidr
  availability_zones = var.availability_zones

  enable_nat_gateway = true
  single_nat_gateway = true

  tags = var.common_tags
}

# Security Module
module "security" {
  source = "./modules/security"

  name_prefix = local.name_prefix
  vpc_id      = module.network.vpc_id
  vpc_cidr    = module.network.vpc_cidr

  tags = var.common_tags
}

# Secrets Module (disabled - using environment variables instead)
# module "secrets" {
#   source = "./modules/secrets"
#
#   name_prefix = local.name_prefix
#   aws_region  = var.aws_region
#
#   db_password         = var.db_password
#   kakao_rest_api_key  = var.kakao_rest_api_key
#   kakao_javascript_key = var.kakao_javascript_key
#   openai_api_key      = var.openai_api_key
#
#   tags = var.common_tags
# }

# ECR Module
module "ecr" {
  source = "./modules/ecr"

  name_prefix = local.name_prefix

  tags = var.common_tags
}

# RDS Module - Disabled (Using Supabase instead)
# module "rds" {
#   source = "./modules/rds"
#
#   name_prefix       = local.name_prefix
#   vpc_id            = module.network.vpc_id
#   subnet_ids        = module.network.private_subnet_ids
#   security_group_id = module.security.rds_security_group_id
#
#   db_instance_class = var.db_instance_class
#   db_name           = var.db_name
#   db_username       = var.db_username
#   db_password       = var.db_password
#
#   multi_az            = true
#   skip_final_snapshot = false
#
#   tags = var.common_tags
# }

# ALB Module
module "alb" {
  source = "./modules/alb"

  name_prefix       = local.name_prefix
  vpc_id            = module.network.vpc_id
  subnet_ids        = module.network.public_subnet_ids
  security_group_id = module.security.alb_security_group_id

  enable_deletion_protection = true
  enable_https               = var.enable_https
  ssl_certificate_arn        = var.ssl_certificate_arn

  tags = var.common_tags
}

# SSM Parameter Store Module
module "ssm" {
  source = "./modules/ssm"

  project_name = var.project_name
  environment  = "prod"

  # Database configuration
  db_host     = var.db_host
  db_port     = tostring(var.db_port)
  db_name     = var.db_name
  db_username = var.db_username
  db_password = var.db_password

  # API keys
  openai_api_key       = var.openai_api_key
  kakao_rest_api_key   = var.kakao_rest_api_key
  kakao_javascript_key = var.kakao_javascript_key

  tags = var.common_tags
}

# ECS Module
module "ecs" {
  source = "./modules/ecs"

  name_prefix       = local.name_prefix
  vpc_id            = module.network.vpc_id
  subnet_ids        = module.network.public_subnet_ids
  security_group_id = module.security.ecs_security_group_id

  backend_target_group_arn  = module.alb.backend_target_group_arn
  frontend_target_group_arn = module.alb.frontend_target_group_arn

  alb_dns_name = module.alb.alb_dns_name

  backend_image  = "${module.ecr.backend_repository_url}:latest"
  frontend_image = "${module.ecr.frontend_repository_url}:latest"

  backend_cpu     = var.backend_cpu
  backend_memory  = var.backend_memory
  frontend_cpu    = var.frontend_cpu
  frontend_memory = var.frontend_memory

  desired_count = var.desired_count
  min_capacity  = var.min_capacity
  max_capacity  = var.max_capacity

  # Database configuration (not used when use_ssm_parameters=true)
  db_host     = var.db_host
  db_port     = var.db_port
  db_name     = var.db_name
  db_username = var.db_username
  db_password = var.db_password

  # Secrets Manager integration (disabled)
  use_secrets_manager    = false
  db_password_secret_arn = ""
  kakao_keys_secret_arn  = ""
  openai_key_secret_arn  = ""

  # SSM Parameter Store integration (enabled)
  use_ssm_parameters           = true
  ssm_db_host_arn              = module.ssm.db_host_arn
  ssm_db_port_arn              = module.ssm.db_port_arn
  ssm_db_name_arn              = module.ssm.db_name_arn
  ssm_db_username_arn          = module.ssm.db_username_arn
  ssm_db_password_arn          = module.ssm.db_password_arn
  ssm_openai_api_key_arn       = module.ssm.openai_api_key_arn
  ssm_kakao_rest_api_key_arn   = module.ssm.kakao_rest_api_key_arn
  ssm_kakao_javascript_key_arn = module.ssm.kakao_javascript_key_arn
  ssm_parameter_arns           = module.ssm.all_parameter_arns

  # API keys (not used when use_ssm_parameters=true)
  openai_api_key       = var.openai_api_key
  kakao_rest_api_key   = var.kakao_rest_api_key
  kakao_javascript_key = var.kakao_javascript_key

  spring_profiles_active = var.spring_profiles_active
  chroma_enabled         = var.chroma_enabled
  chroma_db_url          = var.chroma_db_url
  chroma_collection_name = var.chroma_collection_name

  tags = var.common_tags

  depends_on = [module.ssm]
}

# Monitoring Module
module "monitoring" {
  source = "./modules/monitoring"

  name_prefix = local.name_prefix
  alarm_email = var.alarm_email

  # ECS Monitoring
  ecs_cluster_name          = module.ecs.cluster_name
  ecs_backend_service_name  = module.ecs.backend_service_name
  ecs_frontend_service_name = module.ecs.frontend_service_name

  # ALB Monitoring
  alb_arn_suffix                   = module.alb.alb_arn_suffix
  backend_target_group_arn_suffix  = module.alb.backend_target_group_arn_suffix
  frontend_target_group_arn_suffix = module.alb.frontend_target_group_arn_suffix

  # RDS Monitoring - Disabled (Using Supabase)
  db_instance_id = ""

  tags = var.common_tags
}

# CodeDeploy Module (Blue/Green Deployment)
module "codedeploy" {
  source = "./modules/codedeploy"

  name_prefix = local.name_prefix

  # ECS Configuration
  ecs_cluster_name      = module.ecs.cluster_name
  backend_service_name  = module.ecs.backend_service_name
  frontend_service_name = module.ecs.frontend_service_name

  # Target Groups for Blue/Green
  backend_target_group_name        = module.alb.backend_target_group_name
  backend_green_target_group_name  = module.alb.backend_green_target_group_name
  frontend_target_group_name       = module.alb.frontend_target_group_name
  frontend_green_target_group_name = module.alb.frontend_green_target_group_name

  # Listener ARN (use HTTPS if enabled, otherwise HTTP)
  listener_arn = var.enable_https ? module.alb.https_listener_arn : module.alb.http_listener_arn

  # Deployment Configuration
  deployment_config     = "CodeDeployDefault.ECSLinear10PercentEvery1Minutes"
  termination_wait_time = 5

  tags = var.common_tags

  depends_on = [module.ecs]
}

# GitHub OIDC Module (for CI/CD)
module "github_oidc" {
  source = "./modules/github-oidc"

  name_prefix = local.name_prefix
  github_org  = var.github_org
  github_repo = var.github_repo

  tags = var.common_tags
}
