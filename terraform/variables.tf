# AWS Configuration
variable "aws_region" {
  description = "AWS region for all resources"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Project name for resource naming"
  type        = string
  default     = "lian-date"
}

# Network Configuration
variable "vpc_cidr" {
  description = "CIDR block for VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "Availability zones for the region"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}

# Application Configuration
variable "backend_cpu" {
  description = "CPU units for backend Fargate task"
  type        = number
  default     = 256
}

variable "backend_memory" {
  description = "Memory for backend Fargate task (MiB)"
  type        = number
  default     = 512
}

variable "frontend_cpu" {
  description = "CPU units for frontend Fargate task"
  type        = number
  default     = 256
}

variable "frontend_memory" {
  description = "Memory for frontend Fargate task (MiB)"
  type        = number
  default     = 512
}

# ECS Configuration
variable "desired_count" {
  description = "Desired number of ECS tasks"
  type        = number
  default     = 1
}

variable "min_capacity" {
  description = "Minimum number of tasks for auto scaling"
  type        = number
  default     = 1
}

variable "max_capacity" {
  description = "Maximum number of tasks for auto scaling"
  type        = number
  default     = 4
}

variable "spring_profiles_active" {
  description = "Spring Boot active profiles"
  type        = string
  default     = "prod"
}

#
# Optional: Chroma(Vector DB) configuration
#
variable "chroma_enabled" {
  description = "Enable Chroma integration (vector DB) in backend"
  type        = bool
  default     = false
}

variable "chroma_db_url" {
  description = "Chroma base URL for backend (only used when chroma_enabled=true)"
  type        = string
  default     = ""
}

variable "chroma_collection_name" {
  description = "Chroma collection name for place memory"
  type        = string
  default     = "date_places"
}

# Database Configuration (Supabase)
variable "db_host" {
  description = "Database host (e.g., Supabase pooler URL)"
  type        = string
  sensitive   = true
}

variable "db_port" {
  description = "Database port"
  type        = number
  default     = 6543
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "dateclick"
}

variable "db_username" {
  description = "Database username"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "Database password"
  type        = string
  sensitive   = true
}

# RDS Configuration - Disabled (keeping for reference)
# variable "db_instance_class" {
#   description = "RDS instance class"
#   type        = string
#   default     = "db.t4g.micro"
# }

# Monitoring Configuration
variable "alarm_email" {
  description = "Email address for CloudWatch alarm notifications"
  type        = string
}

# GitHub Configuration (for CI/CD)
variable "github_org" {
  description = "GitHub organization or username"
  type        = string
  default     = "your-github-org"
}

variable "github_repo" {
  description = "GitHub repository name"
  type        = string
  default     = "lian-date-app"
}

# HTTPS Configuration
variable "enable_https" {
  description = "Enable HTTPS listener on ALB"
  type        = bool
  default     = false
}

variable "ssl_certificate_arn" {
  description = "ARN of SSL certificate for HTTPS (required if enable_https is true)"
  type        = string
  default     = ""
}

# Secrets Manager Configuration
variable "use_secrets_manager" {
  description = "Use AWS Secrets Manager for sensitive credentials"
  type        = bool
  default     = false
}

variable "kakao_rest_api_key" {
  description = "Kakao REST API key"
  type        = string
  sensitive   = true
  default     = ""
}

variable "kakao_javascript_key" {
  description = "Kakao JavaScript key"
  type        = string
  sensitive   = true
  default     = ""
}

variable "openai_api_key" {
  description = "OpenAI API key"
  type        = string
  sensitive   = true
  default     = ""
}

# Tags
variable "common_tags" {
  description = "Common tags for all resources"
  type        = map(string)
  default = {
    Project   = "lian-date"
    ManagedBy = "Terraform"
  }
}
