resource "aws_s3_bucket" "backup" {
  bucket        = var.bucket_name
  force_destroy = var.force_destroy

  tags = var.bucket_tags
}

resource "aws_s3_bucket_versioning" "backup" {

  bucket = aws_s3_bucket.backup.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "backup" {
  bucket = aws_s3_bucket.backup.id

  rule {
    bucket_key_enabled = var.bucket_key_enabled

    apply_server_side_encryption_by_default {
      sse_algorithm     = var.sse_algorithm
    }
  }
}

resource "aws_s3_bucket_acl" "backup" {
  bucket = aws_s3_bucket.backup.id
  acl    = "private"
}

resource "aws_s3_bucket_lifecycle_configuration" "backup_lifecycle_configuration" {
  rule {
    id      = "delete_old_backups"
    status  = "Enabled"

    expiration {
      days = 120
    }

    noncurrent_version_expiration {
      noncurrent_days = 120
    }
  }

  bucket = aws_s3_bucket.backup.id
}