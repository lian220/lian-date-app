# AWS Configuration
variable "aws_region" {
  description = "AWS region for all resources"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment name (local, prod)"
  type        = string
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

# Database Configuration
variable "db_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t4g.micro"
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "dateclick"
}

variable "db_username" {
  description = "Database master username"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "Database master password"
  type        = string
  sensitive   = true
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
