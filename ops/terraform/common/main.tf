data "aws_iam_policy_document" "s3_backup_sync" {

  statement {
    sid = "SyncCommandPermissions"

    actions = [
      "s3:ListBucket",
      "s3:ListObjectsV2",
      "s3:PutObject"
    ]

    resources = [
      "arn:aws:s3:::${var.sandbox_bucket_name}",
      "arn:aws:s3:::${var.production_bucket_name}",
      "arn:aws:s3:::${var.sandbox_bucket_name}/*",
      "arn:aws:s3:::${var.production_bucket_name}/*",
    ]
  }
}

resource "aws_iam_policy" "s3_backup_sync" {
  name   = "s3_backup_sync"
  path   = "/"
  policy = data.aws_iam_policy_document.s3_backup_sync.json
}

resource "aws_iam_user" "s3_backup_sync" {
  name = "script-s3-sync"
  tags = {
    purpose = "s3_backup_sync"
    env     = "production"
  }
}

resource "aws_iam_user_policy_attachment" "s3_backup_sync" {
  user       = aws_iam_user.s3_backup_sync.name
  policy_arn = aws_iam_policy.s3_backup_sync.arn
}
