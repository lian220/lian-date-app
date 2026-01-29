variable "name_prefix" {
  description = "Prefix for resource names"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID"
  type        = string
}

variable "subnet_ids" {
  description = "Public subnet IDs for ECS tasks (for external database connectivity)"
  type        = list(string)
}

variable "security_group_id" {
  description = "Security group ID for ECS tasks"
  type        = string
}

variable "backend_target_group_arn" {
  description = "Backend ALB target group ARN"
  type        = string
}

variable "frontend_target_group_arn" {
  description = "Frontend ALB target group ARN"
  type        = string
}

variable "backend_image" {
  description = "Backend Docker image"
  type        = string
}

variable "frontend_image" {
  description = "Frontend Docker image"
  type        = string
}

variable "backend_cpu" {
  description = "CPU units for backend task"
  type        = number
  default     = 512
}

variable "backend_memory" {
  description = "Memory (MB) for backend task"
  type        = number
  default     = 1024
}

variable "frontend_cpu" {
  description = "CPU units for frontend task"
  type        = number
  default     = 256
}

variable "frontend_memory" {
  description = "Memory (MB) for frontend task"
  type        = number
  default     = 512
}

variable "desired_count" {
  description = "Desired number of tasks"
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

variable "db_host" {
  description = "Database host"
  type        = string
}

variable "db_port" {
  description = "Database port"
  type        = number
  default     = 5432
}

variable "db_name" {
  description = "Database name"
  type        = string
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

variable "spring_profiles_active" {
  description = "Spring Boot active profiles"
  type        = string
  default     = "local"
}

variable "chroma_enabled" {
  description = "Whether to enable Chroma integration in backend"
  type        = bool
  default     = false
}

variable "chroma_db_url" {
  description = "Chroma base URL (e.g., http://chroma:8000 or https://...)"
  type        = string
  default     = ""
}

variable "chroma_collection_name" {
  description = "Chroma collection name for place memory"
  type        = string
  default     = "date_places"
}

variable "alb_dns_name" {
  description = "ALB DNS name for frontend API URL"
  type        = string
}

variable "db_password_secret_arn" {
  description = "ARN of the database password secret in Secrets Manager"
  type        = string
  default     = ""
}

variable "kakao_keys_secret_arn" {
  description = "ARN of the Kakao API keys secret in Secrets Manager"
  type        = string
  default     = ""
}

variable "openai_key_secret_arn" {
  description = "ARN of the OpenAI API key secret in Secrets Manager"
  type        = string
  default     = ""
}

variable "use_secrets_manager" {
  description = "Whether to use Secrets Manager for sensitive values"
  type        = bool
  default     = false
}

variable "use_ssm_parameters" {
  description = "Whether to use SSM Parameter Store for sensitive values"
  type        = bool
  default     = false
}

# SSM Parameter ARNs
variable "ssm_db_host_arn" {
  description = "ARN of the DB host SSM parameter"
  type        = string
  default     = ""
}

variable "ssm_db_port_arn" {
  description = "ARN of the DB port SSM parameter"
  type        = string
  default     = ""
}

variable "ssm_db_name_arn" {
  description = "ARN of the DB name SSM parameter"
  type        = string
  default     = ""
}

variable "ssm_db_username_arn" {
  description = "ARN of the DB username SSM parameter"
  type        = string
  default     = ""
}

variable "ssm_db_password_arn" {
  description = "ARN of the DB password SSM parameter"
  type        = string
  default     = ""
}

variable "ssm_openai_api_key_arn" {
  description = "ARN of the OpenAI API key SSM parameter"
  type        = string
  default     = ""
}

variable "ssm_kakao_rest_api_key_arn" {
  description = "ARN of the Kakao REST API key SSM parameter"
  type        = string
  default     = ""
}

variable "ssm_kakao_javascript_key_arn" {
  description = "ARN of the Kakao JavaScript key SSM parameter"
  type        = string
  default     = ""
}

variable "ssm_parameter_arns" {
  description = "List of all SSM parameter ARNs for IAM policy"
  type        = list(string)
  default     = []
}

variable "tags" {
  description = "Tags to apply to resources"
  type        = map(string)
  default     = {}
}

variable "openai_api_key" {
  description = "OpenAI API key (used when use_secrets_manager=false)"
  type        = string
  sensitive   = true
  default     = ""
}

variable "kakao_rest_api_key" {
  description = "Kakao REST API key (used when use_secrets_manager=false)"
  type        = string
  sensitive   = true
  default     = ""
}

variable "kakao_javascript_key" {
  description = "Kakao JavaScript key (used when use_secrets_manager=false)"
  type        = string
  sensitive   = true
  default     = ""
}
