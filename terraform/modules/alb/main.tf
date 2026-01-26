# Application Load Balancer
resource "aws_lb" "main" {
  name               = "${var.name_prefix}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [var.security_group_id]
  subnets            = var.subnet_ids

  enable_deletion_protection       = var.enable_deletion_protection
  enable_http2                     = true
  enable_cross_zone_load_balancing = true

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-alb"
    }
  )
}

# Target Group for Backend
resource "aws_lb_target_group" "backend" {
  name     = "${var.name_prefix}-backend-tg"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = var.vpc_id

  target_type = "ip"

  health_check {
    enabled             = true
    healthy_threshold   = 2
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    path                = "/health"
    matcher             = "200"
    protocol            = "HTTP"
  }

  deregistration_delay = 30

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-backend-tg"
    }
  )
}

# Target Group for Frontend
resource "aws_lb_target_group" "frontend" {
  name     = "${var.name_prefix}-frontend-tg"
  port     = 3000
  protocol = "HTTP"
  vpc_id   = var.vpc_id

  target_type = "ip"

  health_check {
    enabled             = true
    healthy_threshold   = 2
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    path                = "/"
    matcher             = "200"
    protocol            = "HTTP"
  }

  deregistration_delay = 30

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-frontend-tg"
    }
  )
}

# Green Target Group for Backend (Blue/Green Deployment)
resource "aws_lb_target_group" "backend_green" {
  name     = "${var.name_prefix}-backend-tg-green"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = var.vpc_id

  target_type = "ip"

  health_check {
    enabled             = true
    healthy_threshold   = 2
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    path                = "/health"
    matcher             = "200"
    protocol            = "HTTP"
  }

  deregistration_delay = 30

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-backend-tg-green"
    }
  )
}

# Green Target Group for Frontend (Blue/Green Deployment)
resource "aws_lb_target_group" "frontend_green" {
  name     = "${var.name_prefix}-frontend-tg-green"
  port     = 3000
  protocol = "HTTP"
  vpc_id   = var.vpc_id

  target_type = "ip"

  health_check {
    enabled             = true
    healthy_threshold   = 2
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    path                = "/"
    matcher             = "200"
    protocol            = "HTTP"
  }

  deregistration_delay = 30

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-frontend-tg-green"
    }
  )
}

# HTTP Listener (Port 80)
# If HTTPS is enabled, redirect to HTTPS; otherwise, serve HTTP
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = var.enable_https ? "redirect" : "fixed-response"

    dynamic "redirect" {
      for_each = var.enable_https ? [1] : []
      content {
        port        = "443"
        protocol    = "HTTPS"
        status_code = "HTTP_301"
      }
    }

    dynamic "fixed_response" {
      for_each = var.enable_https ? [] : [1]
      content {
        content_type = "text/plain"
        message_body = "Not Found"
        status_code  = "404"
      }
    }
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-http-listener"
    }
  )
}

# HTTPS Listener (Port 443)
resource "aws_lb_listener" "https" {
  count = var.enable_https ? 1 : 0

  load_balancer_arn = aws_lb.main.arn
  port              = 443
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-TLS13-1-2-2021-06"
  certificate_arn   = var.ssl_certificate_arn

  default_action {
    type = "fixed-response"

    fixed_response {
      content_type = "text/plain"
      message_body = "Not Found"
      status_code  = "404"
    }
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-https-listener"
    }
  )
}

# Listener Rules for HTTP (when HTTPS is disabled)
resource "aws_lb_listener_rule" "backend_api_http" {
  count = var.enable_https ? 0 : 1

  listener_arn = aws_lb_listener.http.arn
  priority     = 100

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.backend.arn
  }

  condition {
    path_pattern {
      # Backend API paths
      # - 실제 API: /v1/*
      # - 헬스체크: /health
      # - (선택) /api/* 프록시/레거시 경로
      # - Swagger/OpenAPI
      values = [
        "/v1/*",
        "/api/*",
        "/health",
        "/swagger-ui*",
        "/v1/api-docs*",
      ]
    }
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-backend-api-http-rule"
    }
  )
}

resource "aws_lb_listener_rule" "frontend_http" {
  count = var.enable_https ? 0 : 1

  listener_arn = aws_lb_listener.http.arn
  priority     = 200

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.frontend.arn
  }

  condition {
    path_pattern {
      values = ["/*"]
    }
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-frontend-http-rule"
    }
  )
}

# Listener Rules for HTTPS (when HTTPS is enabled)
resource "aws_lb_listener_rule" "backend_api_https" {
  count = var.enable_https ? 1 : 0

  listener_arn = aws_lb_listener.https[0].arn
  priority     = 100

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.backend.arn
  }

  condition {
    path_pattern {
      values = [
        "/v1/*",
        "/api/*",
        "/health",
        "/swagger-ui*",
        "/v1/api-docs*",
      ]
    }
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-backend-api-https-rule"
    }
  )
}

resource "aws_lb_listener_rule" "frontend_https" {
  count = var.enable_https ? 1 : 0

  listener_arn = aws_lb_listener.https[0].arn
  priority     = 200

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.frontend.arn
  }

  condition {
    path_pattern {
      values = ["/*"]
    }
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-frontend-https-rule"
    }
  )
}
