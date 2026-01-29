# Task Execution Role (ECS가 ECR에서 이미지 pull하고 로그 전송)
resource "aws_iam_role" "task_execution" {
  name = "${var.name_prefix}-ecs-task-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-ecs-task-execution-role"
    }
  )
}

# Task Execution Role Policy Attachment
resource "aws_iam_role_policy_attachment" "task_execution" {
  role       = aws_iam_role.task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Task Execution Role - CloudWatch Logs Policy
resource "aws_iam_role_policy" "task_execution_cloudwatch" {
  name = "${var.name_prefix}-ecs-task-execution-cloudwatch"
  role = aws_iam_role.task_execution.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = [
          aws_cloudwatch_log_group.backend.arn,
          "${aws_cloudwatch_log_group.backend.arn}:*",
          aws_cloudwatch_log_group.frontend.arn,
          "${aws_cloudwatch_log_group.frontend.arn}:*"
        ]
      }
    ]
  })
}

# Task Execution Role - Secrets Manager Policy
resource "aws_iam_role_policy" "task_execution_secrets" {
  count = var.use_secrets_manager ? 1 : 0

  name = "${var.name_prefix}-ecs-task-execution-secrets"
  role = aws_iam_role.task_execution.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "secretsmanager:GetSecretValue",
          "secretsmanager:DescribeSecret"
        ]
        Resource = compact([
          var.db_password_secret_arn,
          var.kakao_keys_secret_arn,
          var.openai_key_secret_arn
        ])
      },
      {
        Effect = "Allow"
        Action = [
          "kms:Decrypt",
          "kms:DescribeKey"
        ]
        Resource = "*"
        Condition = {
          StringEquals = {
            "kms:ViaService" = [
              "secretsmanager.${data.aws_region.current.name}.amazonaws.com"
            ]
          }
        }
      }
    ]
  })
}

# Task Execution Role - SSM Parameter Store Policy
resource "aws_iam_role_policy" "task_execution_ssm" {
  count = var.use_ssm_parameters ? 1 : 0

  name = "${var.name_prefix}-ecs-task-execution-ssm"
  role = aws_iam_role.task_execution.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ssm:GetParameters",
          "ssm:GetParameter",
          "ssm:GetParametersByPath"
        ]
        Resource = var.ssm_parameter_arns
      },
      {
        Effect = "Allow"
        Action = [
          "kms:Decrypt"
        ]
        Resource = "*"
        Condition = {
          StringEquals = {
            "kms:ViaService" = [
              "ssm.${data.aws_region.current.name}.amazonaws.com"
            ]
          }
        }
      }
    ]
  })
}

# Task Role (컨테이너 내부에서 AWS 서비스 접근)
resource "aws_iam_role" "task" {
  name = "${var.name_prefix}-ecs-task-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-ecs-task-role"
    }
  )
}

# Task Role - CloudWatch Logs Policy
resource "aws_iam_role_policy" "task_cloudwatch" {
  name = "${var.name_prefix}-ecs-task-cloudwatch"
  role = aws_iam_role.task.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "*"
      }
    ]
  })
}
