variable "name_prefix" {
  description = "Prefix for resource names"
  type        = string
}

variable "image_tag_mutability" {
  description = "Tag mutability setting for the repository (MUTABLE or IMMUTABLE)"
  type        = string
  default     = "MUTABLE"
}

variable "scan_on_push" {
  description = "Enable scan on push for images"
  type        = bool
  default     = true
}

variable "lifecycle_policy_days" {
  description = "Number of days to keep untagged images"
  type        = number
  default     = 7
}

variable "tags" {
  description = "Common tags for all resources"
  type        = map(string)
  default     = {}
}
