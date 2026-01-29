# ALB Outputs
output "alb_id" {
  description = "The ID of the load balancer"
  value       = aws_lb.main.id
}

output "alb_arn" {
  description = "The ARN of the load balancer"
  value       = aws_lb.main.arn
}

output "alb_dns_name" {
  description = "The DNS name of the load balancer"
  value       = aws_lb.main.dns_name
}

output "alb_zone_id" {
  description = "The zone ID of the load balancer"
  value       = aws_lb.main.zone_id
}

output "alb_arn_suffix" {
  description = "The ARN suffix for use with CloudWatch metrics"
  value       = aws_lb.main.arn_suffix
}

# Target Group Outputs
output "backend_target_group_arn" {
  description = "ARN of the backend target group"
  value       = aws_lb_target_group.backend.arn
}

output "backend_target_group_arn_suffix" {
  description = "ARN suffix of the backend target group for CloudWatch metrics"
  value       = aws_lb_target_group.backend.arn_suffix
}

output "frontend_target_group_arn" {
  description = "ARN of the frontend target group"
  value       = aws_lb_target_group.frontend.arn
}

output "frontend_target_group_arn_suffix" {
  description = "ARN suffix of the frontend target group for CloudWatch metrics"
  value       = aws_lb_target_group.frontend.arn_suffix
}

# Green Target Group Outputs (Blue/Green Deployment)
output "backend_green_target_group_arn" {
  description = "ARN of the backend green target group"
  value       = aws_lb_target_group.backend_green.arn
}

output "backend_green_target_group_name" {
  description = "Name of the backend green target group"
  value       = aws_lb_target_group.backend_green.name
}

output "frontend_green_target_group_arn" {
  description = "ARN of the frontend green target group"
  value       = aws_lb_target_group.frontend_green.arn
}

output "frontend_green_target_group_name" {
  description = "Name of the frontend green target group"
  value       = aws_lb_target_group.frontend_green.name
}

# Target Group Names (for CodeDeploy)
output "backend_target_group_name" {
  description = "Name of the backend target group"
  value       = aws_lb_target_group.backend.name
}

output "frontend_target_group_name" {
  description = "Name of the frontend target group"
  value       = aws_lb_target_group.frontend.name
}

# Listener ARNs (for CodeDeploy)
output "http_listener_arn" {
  description = "ARN of the HTTP listener"
  value       = aws_lb_listener.http.arn
}

output "https_listener_arn" {
  description = "ARN of the HTTPS listener (empty if HTTPS not enabled)"
  value       = var.enable_https ? aws_lb_listener.https[0].arn : ""
}
