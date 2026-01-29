# =============================================================================
# SSM Parameter Store Module
# =============================================================================
# Production secrets management using AWS Systems Manager Parameter Store
# Parameters are created with SecureString type for encryption at rest

# -----------------------------------------------------------------------------
# Database Parameters
# -----------------------------------------------------------------------------
resource "aws_ssm_parameter" "db_host" {
  name        = "/${var.project_name}/${var.environment}/db/host"
  description = "Database host"
  type        = "SecureString"
  value       = var.db_host
  tier        = "Standard"
  overwrite   = true

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-db-host"
  })

  lifecycle {
    ignore_changes = [value]
  }
}

resource "aws_ssm_parameter" "db_port" {
  name        = "/${var.project_name}/${var.environment}/db/port"
  description = "Database port"
  type        = "String"
  value       = var.db_port
  tier        = "Standard"
  overwrite   = true

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-db-port"
  })

  lifecycle {
    ignore_changes = [value]
  }
}

resource "aws_ssm_parameter" "db_name" {
  name        = "/${var.project_name}/${var.environment}/db/name"
  description = "Database name"
  type        = "String"
  value       = var.db_name
  tier        = "Standard"
  overwrite   = true

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-db-name"
  })

  lifecycle {
    ignore_changes = [value]
  }
}

resource "aws_ssm_parameter" "db_username" {
  name        = "/${var.project_name}/${var.environment}/db/username"
  description = "Database username"
  type        = "SecureString"
  value       = var.db_username
  tier        = "Standard"
  overwrite   = true

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-db-username"
  })

  lifecycle {
    ignore_changes = [value]
  }
}

resource "aws_ssm_parameter" "db_password" {
  name        = "/${var.project_name}/${var.environment}/db/password"
  description = "Database password"
  type        = "SecureString"
  value       = var.db_password
  tier        = "Standard"
  overwrite   = true

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-db-password"
  })

  lifecycle {
    ignore_changes = [value]
  }
}

# -----------------------------------------------------------------------------
# API Keys Parameters
# -----------------------------------------------------------------------------
resource "aws_ssm_parameter" "openai_api_key" {
  name        = "/${var.project_name}/${var.environment}/api/openai-api-key"
  description = "OpenAI API Key"
  type        = "SecureString"
  value       = var.openai_api_key
  tier        = "Standard"
  overwrite   = true

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-openai-api-key"
  })

  lifecycle {
    ignore_changes = [value]
  }
}

resource "aws_ssm_parameter" "kakao_rest_api_key" {
  name        = "/${var.project_name}/${var.environment}/api/kakao-rest-api-key"
  description = "Kakao REST API Key"
  type        = "SecureString"
  value       = var.kakao_rest_api_key
  tier        = "Standard"
  overwrite   = true

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-kakao-rest-api-key"
  })

  lifecycle {
    ignore_changes = [value]
  }
}

resource "aws_ssm_parameter" "kakao_javascript_key" {
  name        = "/${var.project_name}/${var.environment}/api/kakao-javascript-key"
  description = "Kakao JavaScript Key"
  type        = "SecureString"
  value       = var.kakao_javascript_key
  tier        = "Standard"
  overwrite   = true

  tags = merge(var.tags, {
    Name = "${var.project_name}-${var.environment}-kakao-javascript-key"
  })

  lifecycle {
    ignore_changes = [value]
  }
}
