# ECS Cluster Outputs
output "cluster_id" {
  description = "The ID of the ECS cluster"
  value       = aws_ecs_cluster.main.id
}

output "cluster_name" {
  description = "The name of the ECS cluster"
  value       = aws_ecs_cluster.main.name
}

output "cluster_arn" {
  description = "The ARN of the ECS cluster"
  value       = aws_ecs_cluster.main.arn
}

# Backend Service Outputs
output "backend_service_id" {
  description = "The ID of the backend ECS service"
  value       = aws_ecs_service.backend.id
}

output "backend_service_name" {
  description = "The name of the backend ECS service"
  value       = aws_ecs_service.backend.name
}

output "backend_task_definition_arn" {
  description = "The ARN of the backend task definition"
  value       = aws_ecs_task_definition.backend.arn
}

output "backend_log_group_name" {
  description = "The name of the backend CloudWatch log group"
  value       = aws_cloudwatch_log_group.backend.name
}

# Frontend Service Outputs
output "frontend_service_id" {
  description = "The ID of the frontend ECS service"
  value       = aws_ecs_service.frontend.id
}

output "frontend_service_name" {
  description = "The name of the frontend ECS service"
  value       = aws_ecs_service.frontend.name
}

output "frontend_task_definition_arn" {
  description = "The ARN of the frontend task definition"
  value       = aws_ecs_task_definition.frontend.arn
}

output "frontend_log_group_name" {
  description = "The name of the frontend CloudWatch log group"
  value       = aws_cloudwatch_log_group.frontend.name
}
