# Local Environment Configuration

environment = "local"
aws_region  = "us-east-1"

# Network Configuration
vpc_cidr = "10.0.0.0/16"
availability_zones = [
  "us-east-1a",
  "us-east-1b"
]

# Application Configuration
backend_cpu    = 256
backend_memory = 512

frontend_cpu    = 256
frontend_memory = 512

# Database Configuration
db_instance_class = "db.t4g.micro"
db_name           = "dateclick"
# db_username and db_password should be provided via environment variables or secure parameter store

# Tags
common_tags = {
  Project     = "lian-date"
  Environment = "local"
  ManagedBy   = "Terraform"
}
