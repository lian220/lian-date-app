variable "name_prefix" {
  description = "Prefix for resource names"
  type        = string
}

variable "alarm_email" {
  description = "Email address for alarm notifications"
  type        = string
}

# ECS Monitoring Variables
variable "ecs_cluster_name" {
  description = "ECS cluster name"
  type        = string
}

variable "ecs_backend_service_name" {
  description = "Backend ECS service name"
  type        = string
}

variable "ecs_frontend_service_name" {
  description = "Frontend ECS service name"
  type        = string
}

# ALB Monitoring Variables
variable "alb_arn_suffix" {
  description = "ALB ARN suffix"
  type        = string
}

variable "backend_target_group_arn_suffix" {
  description = "Backend target group ARN suffix"
  type        = string
}

variable "frontend_target_group_arn_suffix" {
  description = "Frontend target group ARN suffix"
  type        = string
}

# RDS Monitoring Variables
variable "db_instance_id" {
  description = "RDS instance identifier"
  type        = string
}

variable "tags" {
  description = "Tags to apply to resources"
  type        = map(string)
  default     = {}
}
