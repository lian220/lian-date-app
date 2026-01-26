# CloudWatch Log Groups
resource "aws_cloudwatch_log_group" "backend" {
  name              = "/ecs/${var.name_prefix}/backend"
  retention_in_days = 7

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-backend-logs"
    }
  )
}

resource "aws_cloudwatch_log_group" "frontend" {
  name              = "/ecs/${var.name_prefix}/frontend"
  retention_in_days = 7

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-frontend-logs"
    }
  )
}
