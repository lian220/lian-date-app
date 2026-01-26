# =============================================================================
# SSM Parameter Store Module Variables
# =============================================================================

variable "project_name" {
  description = "Project name for resource naming"
  type        = string
}

variable "environment" {
  description = "Environment name (e.g., prod, staging)"
  type        = string
}

variable "tags" {
  description = "Tags to apply to resources"
  type        = map(string)
  default     = {}
}

# -----------------------------------------------------------------------------
# Database Configuration
# -----------------------------------------------------------------------------
variable "db_host" {
  description = "Database host"
  type        = string
  sensitive   = true
}

variable "db_port" {
  description = "Database port"
  type        = string
  default     = "6543"
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

# -----------------------------------------------------------------------------
# API Keys
# -----------------------------------------------------------------------------
variable "openai_api_key" {
  description = "OpenAI API Key"
  type        = string
  sensitive   = true
}

variable "kakao_rest_api_key" {
  description = "Kakao REST API Key"
  type        = string
  sensitive   = true
}

variable "kakao_javascript_key" {
  description = "Kakao JavaScript Key"
  type        = string
  sensitive   = true
}
