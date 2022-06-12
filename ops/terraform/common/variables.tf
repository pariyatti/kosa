variable "sandbox_bucket_name" {
  type        = string
  description = "sandbox-bucket-name"
  default     = "kosa-sandbox-data-backup"
}

variable "production_bucket_name" {
  type        = string
  description = "production-bucket-name"
  default     = "kosa-production-data-backup"
}
