# ALB Security Group
output "alb_security_group_id" {
  description = "ALB security group ID"
  value       = aws_security_group.alb.id
}

output "alb_security_group_name" {
  description = "ALB security group name"
  value       = aws_security_group.alb.name
}

# ECS Security Group
output "ecs_security_group_id" {
  description = "ECS security group ID"
  value       = aws_security_group.ecs.id
}

output "ecs_security_group_name" {
  description = "ECS security group name"
  value       = aws_security_group.ecs.name
}

# RDS Security Group
output "rds_security_group_id" {
  description = "RDS security group ID"
  value       = aws_security_group.rds.id
}

output "rds_security_group_name" {
  description = "RDS security group name"
  value       = aws_security_group.rds.name
}
