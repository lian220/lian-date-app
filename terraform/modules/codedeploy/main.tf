# CodeDeploy IAM Role
resource "aws_iam_role" "codedeploy" {
  name = "${var.name_prefix}-codedeploy-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "codedeploy.amazonaws.com"
        }
      }
    ]
  })

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-codedeploy-role"
    }
  )
}

# Attach AWS managed policy for ECS Blue/Green deployments
resource "aws_iam_role_policy_attachment" "codedeploy_ecs" {
  role       = aws_iam_role.codedeploy.name
  policy_arn = "arn:aws:iam::aws:policy/AWSCodeDeployRoleForECS"
}

# CodeDeploy Application for Backend
resource "aws_codedeploy_app" "backend" {
  name             = "${var.name_prefix}-backend"
  compute_platform = "ECS"

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-backend-codedeploy"
    }
  )

  lifecycle {
    ignore_changes = all
  }
}

# CodeDeploy Application for Frontend
resource "aws_codedeploy_app" "frontend" {
  name             = "${var.name_prefix}-frontend"
  compute_platform = "ECS"

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-frontend-codedeploy"
    }
  )

  lifecycle {
    ignore_changes = all
  }
}

# CodeDeploy Deployment Group for Backend
resource "aws_codedeploy_deployment_group" "backend" {
  app_name               = aws_codedeploy_app.backend.name
  deployment_group_name  = "${var.name_prefix}-backend-dg"
  service_role_arn       = aws_iam_role.codedeploy.arn
  deployment_config_name = var.deployment_config

  ecs_service {
    cluster_name = var.ecs_cluster_name
    service_name = var.backend_service_name
  }

  deployment_style {
    deployment_option = "WITH_TRAFFIC_CONTROL"
    deployment_type   = "BLUE_GREEN"
  }

  blue_green_deployment_config {
    deployment_ready_option {
      action_on_timeout = "CONTINUE_DEPLOYMENT"
    }

    terminate_blue_instances_on_deployment_success {
      action                           = "TERMINATE"
      termination_wait_time_in_minutes = var.termination_wait_time
    }
  }

  load_balancer_info {
    target_group_pair_info {
      prod_traffic_route {
        listener_arns = [var.listener_arn]
      }

      target_group {
        name = var.backend_target_group_name
      }

      target_group {
        name = var.backend_green_target_group_name
      }
    }
  }

  auto_rollback_configuration {
    enabled = true
    events  = ["DEPLOYMENT_FAILURE", "DEPLOYMENT_STOP_ON_ALARM"]
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-backend-dg"
    }
  )
}

# CodeDeploy Deployment Group for Frontend
resource "aws_codedeploy_deployment_group" "frontend" {
  app_name               = aws_codedeploy_app.frontend.name
  deployment_group_name  = "${var.name_prefix}-frontend-dg"
  service_role_arn       = aws_iam_role.codedeploy.arn
  deployment_config_name = var.deployment_config

  ecs_service {
    cluster_name = var.ecs_cluster_name
    service_name = var.frontend_service_name
  }

  deployment_style {
    deployment_option = "WITH_TRAFFIC_CONTROL"
    deployment_type   = "BLUE_GREEN"
  }

  blue_green_deployment_config {
    deployment_ready_option {
      action_on_timeout = "CONTINUE_DEPLOYMENT"
    }

    terminate_blue_instances_on_deployment_success {
      action                           = "TERMINATE"
      termination_wait_time_in_minutes = var.termination_wait_time
    }
  }

  load_balancer_info {
    target_group_pair_info {
      prod_traffic_route {
        listener_arns = [var.listener_arn]
      }

      target_group {
        name = var.frontend_target_group_name
      }

      target_group {
        name = var.frontend_green_target_group_name
      }
    }
  }

  auto_rollback_configuration {
    enabled = true
    events  = ["DEPLOYMENT_FAILURE", "DEPLOYMENT_STOP_ON_ALARM"]
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-frontend-dg"
    }
  )
}
