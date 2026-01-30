# Backend Task Definition
resource "aws_ecs_task_definition" "backend" {
  family                   = "${var.name_prefix}-backend"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.backend_cpu
  memory                   = var.backend_memory
  execution_role_arn       = aws_iam_role.task_execution.arn
  task_role_arn            = aws_iam_role.task.arn

  container_definitions = jsonencode([
    {
      name      = "backend"
      image     = var.backend_image
      essential = true

      portMappings = [
        {
          containerPort = 8080
          protocol      = "tcp"
        }
      ]

      # Environment variables (non-sensitive)
      environment = concat(
        [
          {
            name  = "SPRING_PROFILES_ACTIVE"
            value = var.spring_profiles_active
          },
          {
            name  = "CHROMA_ENABLED"
            value = tostring(var.chroma_enabled)
          }
        ],
        var.chroma_db_url != "" ? [
          {
            name  = "CHROMA_DB_URL"
            value = var.chroma_db_url
          }
        ] : [],
        var.chroma_collection_name != "" ? [
          {
            name  = "CHROMA_COLLECTION_NAME"
            value = var.chroma_collection_name
          }
        ] : [],
        # Include DB config as env vars only when NOT using SSM or Secrets Manager
        !var.use_ssm_parameters && !var.use_secrets_manager ? [
          {
            name  = "DB_HOST"
            value = var.db_host
          },
          {
            name  = "DB_PORT"
            value = tostring(var.db_port)
          },
          {
            name  = "DB_NAME"
            value = var.db_name
          },
          {
            name  = "DB_USERNAME"
            value = var.db_username
          },
          {
            name  = "DB_PASSWORD"
            value = var.db_password
          },
          {
            name  = "OPENAI_API_KEY"
            value = var.openai_api_key
          },
          {
            name  = "KAKAO_REST_API_KEY"
            value = var.kakao_rest_api_key
          },
          {
            name  = "KAKAO_JAVASCRIPT_KEY"
            value = var.kakao_javascript_key
          }
        ] : []
      )

      # Secrets from SSM Parameter Store
      secrets = var.use_ssm_parameters ? [
        {
          name      = "DB_HOST"
          valueFrom = var.ssm_db_host_arn
        },
        {
          name      = "DB_PORT"
          valueFrom = var.ssm_db_port_arn
        },
        {
          name      = "DB_NAME"
          valueFrom = var.ssm_db_name_arn
        },
        {
          name      = "DB_USERNAME"
          valueFrom = var.ssm_db_username_arn
        },
        {
          name      = "DB_PASSWORD"
          valueFrom = var.ssm_db_password_arn
        },
        {
          name      = "OPENAI_API_KEY"
          valueFrom = var.ssm_openai_api_key_arn
        },
        {
          name      = "KAKAO_REST_API_KEY"
          valueFrom = var.ssm_kakao_rest_api_key_arn
        },
        {
          name      = "KAKAO_JAVASCRIPT_KEY"
          valueFrom = var.ssm_kakao_javascript_key_arn
        }
      ] : var.use_secrets_manager ? [
        {
          name      = "DB_PASSWORD"
          valueFrom = var.db_password_secret_arn
        },
        {
          name      = "KAKAO_REST_API_KEY"
          valueFrom = "${var.kakao_keys_secret_arn}:rest_api_key::"
        },
        {
          name      = "KAKAO_JAVASCRIPT_KEY"
          valueFrom = "${var.kakao_keys_secret_arn}:javascript_key::"
        },
        {
          name      = "OPENAI_API_KEY"
          valueFrom = var.openai_key_secret_arn
        }
      ] : []

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.backend.name
          "awslogs-region"        = data.aws_region.current.name
          "awslogs-stream-prefix" = "ecs"
        }
      }

      healthCheck = {
        command     = ["CMD-SHELL", "wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1"]
        interval    = 30
        timeout     = 5
        retries     = 3
        startPeriod = 120
      }
    }
  ])

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-backend-task"
    }
  )
}

# Frontend Task Definition
resource "aws_ecs_task_definition" "frontend" {
  family                   = "${var.name_prefix}-frontend"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.frontend_cpu
  memory                   = var.frontend_memory
  execution_role_arn       = aws_iam_role.task_execution.arn
  task_role_arn            = aws_iam_role.task.arn

  container_definitions = jsonencode([
    {
      name      = "frontend"
      image     = var.frontend_image
      essential = true

      portMappings = [
        {
          containerPort = 3000
          protocol      = "tcp"
        }
      ]

      environment = [
        {
          name  = "NEXT_PUBLIC_API_URL"
          value = "http://${var.alb_dns_name}"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.frontend.name
          "awslogs-region"        = data.aws_region.current.name
          "awslogs-stream-prefix" = "ecs"
        }
      }

      healthCheck = {
        command     = ["CMD-SHELL", "node -e \"require('http').get('http://localhost:3000', (r) => process.exit(r.statusCode < 400 ? 0 : 1)).on('error', () => process.exit(1))\""]
        interval    = 30
        timeout     = 10
        retries     = 3
        startPeriod = 120
      }
    }
  ])

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-frontend-task"
    }
  )
}
