# SNS Topic
output "sns_topic_arn" {
  description = "SNS topic ARN for alarms"
  value       = aws_sns_topic.alarms.arn
}

output "sns_topic_name" {
  description = "SNS topic name"
  value       = aws_sns_topic.alarms.name
}

# ECS Alarms
output "ecs_backend_cpu_alarm_arn" {
  description = "Backend ECS CPU alarm ARN"
  value       = aws_cloudwatch_metric_alarm.ecs_backend_cpu_high.arn
}

output "ecs_backend_memory_alarm_arn" {
  description = "Backend ECS memory alarm ARN"
  value       = aws_cloudwatch_metric_alarm.ecs_backend_memory_high.arn
}

output "ecs_frontend_cpu_alarm_arn" {
  description = "Frontend ECS CPU alarm ARN"
  value       = aws_cloudwatch_metric_alarm.ecs_frontend_cpu_high.arn
}

output "ecs_frontend_memory_alarm_arn" {
  description = "Frontend ECS memory alarm ARN"
  value       = aws_cloudwatch_metric_alarm.ecs_frontend_memory_high.arn
}

# ALB Alarms
output "alb_unhealthy_backend_alarm_arn" {
  description = "ALB unhealthy backend targets alarm ARN"
  value       = aws_cloudwatch_metric_alarm.alb_unhealthy_targets_backend.arn
}

output "alb_unhealthy_frontend_alarm_arn" {
  description = "ALB unhealthy frontend targets alarm ARN"
  value       = aws_cloudwatch_metric_alarm.alb_unhealthy_targets_frontend.arn
}

output "alb_5xx_alarm_arn" {
  description = "ALB 5xx errors alarm ARN"
  value       = aws_cloudwatch_metric_alarm.alb_5xx_errors.arn
}

# RDS Alarms
output "rds_cpu_alarm_arn" {
  description = "RDS CPU alarm ARN"
  value       = try(aws_cloudwatch_metric_alarm.rds_cpu_high[0].arn, null)
}

output "rds_storage_alarm_arn" {
  description = "RDS storage alarm ARN"
  value       = try(aws_cloudwatch_metric_alarm.rds_storage_low[0].arn, null)
}

output "rds_connections_alarm_arn" {
  description = "RDS connections alarm ARN"
  value       = try(aws_cloudwatch_metric_alarm.rds_connections_high[0].arn, null)
}

output "rds_write_latency_alarm_arn" {
  description = "RDS write latency alarm ARN"
  value       = try(aws_cloudwatch_metric_alarm.rds_write_latency_high[0].arn, null)
}
