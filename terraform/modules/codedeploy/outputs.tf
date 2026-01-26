# CodeDeploy Application Outputs
output "backend_app_name" {
  description = "Name of the backend CodeDeploy application"
  value       = aws_codedeploy_app.backend.name
}

output "frontend_app_name" {
  description = "Name of the frontend CodeDeploy application"
  value       = aws_codedeploy_app.frontend.name
}

# Deployment Group Outputs
output "backend_deployment_group_name" {
  description = "Name of the backend deployment group"
  value       = aws_codedeploy_deployment_group.backend.deployment_group_name
}

output "frontend_deployment_group_name" {
  description = "Name of the frontend deployment group"
  value       = aws_codedeploy_deployment_group.frontend.deployment_group_name
}

# IAM Role Output
output "codedeploy_role_arn" {
  description = "ARN of the CodeDeploy IAM role"
  value       = aws_iam_role.codedeploy.arn
}
