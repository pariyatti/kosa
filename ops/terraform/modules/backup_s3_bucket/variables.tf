variable "bucket_name" {
  type        = string
  description = "Bucket name. If provided, the bucket will be created with this name instead of generating the name from the context"
}

variable "force_destroy" {
  type        = bool
  default     = false
  description = <<-EOT
    When `true`, permits a non-empty S3 bucket to be deleted by first deleting all objects in the bucket.
    THESE OBJECTS ARE NOT RECOVERABLE even if they were versioned and stored in Glacier.
    EOT
}

variable "bucket_key_enabled" {
  type        = bool
  default     = true
  description = <<-EOT
  Set this to true to use Amazon S3 Bucket Keys for SSE-KMS, which reduce the cost of AWS KMS requests.
  For more information, see: https://docs.aws.amazon.com/AmazonS3/latest/userguide/bucket-key.html
  EOT
}

variable "sse_algorithm" {
  type        = string
  default     = "AES256"
  description = "The server-side encryption algorithm to use. Valid values are `AES256` and `aws:kms`"
}

variable "bucket_tags" {
  type        = map(string)
  default     = {}
  description = "Server tags that help in filtering resources while searching"
}