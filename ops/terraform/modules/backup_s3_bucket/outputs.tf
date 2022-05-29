output "bucket_domain_name" {
  value       = aws_s3_bucket.backup.bucket_domain_name
  description = "Domain name of bucket"
}

output "bucket_id" {
  value       = aws_s3_bucket.backup.id
  description = "Bucket Name"
}

output "bucket_arn" {
  value       = aws_s3_bucket.backup.arn
  description = "Bucket ARN"
}