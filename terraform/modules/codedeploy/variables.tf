variable "name_prefix" {
  description = "Prefix for resource names"
  type        = string
}

variable "ecs_cluster_name" {
  description = "Name of the ECS cluster"
  type        = string
}

variable "backend_service_name" {
  description = "Name of the backend ECS service"
  type        = string
}

variable "frontend_service_name" {
  description = "Name of the frontend ECS service"
  type        = string
}

variable "backend_target_group_name" {
  description = "Name of the backend blue target group"
  type        = string
}

variable "backend_green_target_group_name" {
  description = "Name of the backend green target group"
  type        = string
}

variable "frontend_target_group_name" {
  description = "Name of the frontend blue target group"
  type        = string
}

variable "frontend_green_target_group_name" {
  description = "Name of the frontend green target group"
  type        = string
}

variable "listener_arn" {
  description = "ARN of the ALB listener (HTTP or HTTPS)"
  type        = string
}

variable "deployment_config" {
  description = "CodeDeploy deployment configuration name"
  type        = string
  default     = "CodeDeployDefault.ECSLinear10PercentEvery1Minutes"
}

variable "termination_wait_time" {
  description = "Minutes to wait before terminating old tasks after deployment"
  type        = number
  default     = 5
}

variable "tags" {
  description = "Tags to apply to resources"
  type        = map(string)
  default     = {}
}
