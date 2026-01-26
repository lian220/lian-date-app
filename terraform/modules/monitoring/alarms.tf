# ==================================
# ECS Alarms
# ==================================

# Backend ECS Service CPU Utilization
resource "aws_cloudwatch_metric_alarm" "ecs_backend_cpu_high" {
  alarm_name          = "${var.name_prefix}-ecs-backend-cpu-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ECS"
  period              = 300
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "Backend ECS service CPU utilization is above 80%"
  alarm_actions       = [aws_sns_topic.alarms.arn]

  dimensions = {
    ClusterName = var.ecs_cluster_name
    ServiceName = var.ecs_backend_service_name
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-ecs-backend-cpu-alarm"
    }
  )
}

# Backend ECS Service Memory Utilization
resource "aws_cloudwatch_metric_alarm" "ecs_backend_memory_high" {
  alarm_name          = "${var.name_prefix}-ecs-backend-memory-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "MemoryUtilization"
  namespace           = "AWS/ECS"
  period              = 300
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "Backend ECS service memory utilization is above 80%"
  alarm_actions       = [aws_sns_topic.alarms.arn]

  dimensions = {
    ClusterName = var.ecs_cluster_name
    ServiceName = var.ecs_backend_service_name
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-ecs-backend-memory-alarm"
    }
  )
}

# Frontend ECS Service CPU Utilization
resource "aws_cloudwatch_metric_alarm" "ecs_frontend_cpu_high" {
  alarm_name          = "${var.name_prefix}-ecs-frontend-cpu-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ECS"
  period              = 300
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "Frontend ECS service CPU utilization is above 80%"
  alarm_actions       = [aws_sns_topic.alarms.arn]

  dimensions = {
    ClusterName = var.ecs_cluster_name
    ServiceName = var.ecs_frontend_service_name
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-ecs-frontend-cpu-alarm"
    }
  )
}

# Frontend ECS Service Memory Utilization
resource "aws_cloudwatch_metric_alarm" "ecs_frontend_memory_high" {
  alarm_name          = "${var.name_prefix}-ecs-frontend-memory-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "MemoryUtilization"
  namespace           = "AWS/ECS"
  period              = 300
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "Frontend ECS service memory utilization is above 80%"
  alarm_actions       = [aws_sns_topic.alarms.arn]

  dimensions = {
    ClusterName = var.ecs_cluster_name
    ServiceName = var.ecs_frontend_service_name
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-ecs-frontend-memory-alarm"
    }
  )
}

# ==================================
# ALB Alarms
# ==================================

# ALB Unhealthy Target Count
resource "aws_cloudwatch_metric_alarm" "alb_unhealthy_targets_backend" {
  alarm_name          = "${var.name_prefix}-alb-unhealthy-targets-backend"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "UnHealthyHostCount"
  namespace           = "AWS/ApplicationELB"
  period              = 60
  statistic           = "Average"
  threshold           = 0
  alarm_description   = "Backend target group has unhealthy targets"
  alarm_actions       = [aws_sns_topic.alarms.arn]

  dimensions = {
    LoadBalancer = var.alb_arn_suffix
    TargetGroup  = var.backend_target_group_arn_suffix
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-alb-unhealthy-backend-alarm"
    }
  )
}

resource "aws_cloudwatch_metric_alarm" "alb_unhealthy_targets_frontend" {
  alarm_name          = "${var.name_prefix}-alb-unhealthy-targets-frontend"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "UnHealthyHostCount"
  namespace           = "AWS/ApplicationELB"
  period              = 60
  statistic           = "Average"
  threshold           = 0
  alarm_description   = "Frontend target group has unhealthy targets"
  alarm_actions       = [aws_sns_topic.alarms.arn]

  dimensions = {
    LoadBalancer = var.alb_arn_suffix
    TargetGroup  = var.frontend_target_group_arn_suffix
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-alb-unhealthy-frontend-alarm"
    }
  )
}

# ALB 5xx Error Rate
resource "aws_cloudwatch_metric_alarm" "alb_5xx_errors" {
  alarm_name          = "${var.name_prefix}-alb-5xx-errors"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "HTTPCode_Target_5XX_Count"
  namespace           = "AWS/ApplicationELB"
  period              = 60
  statistic           = "Sum"
  threshold           = 10
  alarm_description   = "ALB 5xx error count is above 10"
  alarm_actions       = [aws_sns_topic.alarms.arn]
  treat_missing_data  = "notBreaching"

  dimensions = {
    LoadBalancer = var.alb_arn_suffix
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-alb-5xx-alarm"
    }
  )
}

# ==================================
# RDS Alarms
# ==================================

# RDS CPU Utilization
resource "aws_cloudwatch_metric_alarm" "rds_cpu_high" {
  count = var.db_instance_id != "" ? 1 : 0

  alarm_name          = "${var.name_prefix}-rds-cpu-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "CPUUtilization"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "RDS instance CPU utilization is above 80%"
  alarm_actions       = [aws_sns_topic.alarms.arn]

  dimensions = {
    DBInstanceIdentifier = var.db_instance_id
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-rds-cpu-alarm"
    }
  )
}

# RDS Free Storage Space
resource "aws_cloudwatch_metric_alarm" "rds_storage_low" {
  count = var.db_instance_id != "" ? 1 : 0

  alarm_name          = "${var.name_prefix}-rds-storage-low"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = 1
  metric_name         = "FreeStorageSpace"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = 2147483648 # 2GB in bytes
  alarm_description   = "RDS instance free storage space is below 2GB"
  alarm_actions       = [aws_sns_topic.alarms.arn]

  dimensions = {
    DBInstanceIdentifier = var.db_instance_id
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-rds-storage-alarm"
    }
  )
}

# RDS Database Connections
resource "aws_cloudwatch_metric_alarm" "rds_connections_high" {
  count = var.db_instance_id != "" ? 1 : 0

  alarm_name          = "${var.name_prefix}-rds-connections-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "DatabaseConnections"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "RDS instance connection count is above 80"
  alarm_actions       = [aws_sns_topic.alarms.arn]

  dimensions = {
    DBInstanceIdentifier = var.db_instance_id
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-rds-connections-alarm"
    }
  )
}

# RDS Read/Write Latency
resource "aws_cloudwatch_metric_alarm" "rds_write_latency_high" {
  count = var.db_instance_id != "" ? 1 : 0

  alarm_name          = "${var.name_prefix}-rds-write-latency-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "WriteLatency"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = 0.1 # 100ms
  alarm_description   = "RDS write latency is above 100ms"
  alarm_actions       = [aws_sns_topic.alarms.arn]

  dimensions = {
    DBInstanceIdentifier = var.db_instance_id
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-rds-write-latency-alarm"
    }
  )
}
