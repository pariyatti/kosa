output "iam_user_arn" {
  value       = aws_iam_user.s3_backup_sync.arn
  description = "ARN of the created user"
}
