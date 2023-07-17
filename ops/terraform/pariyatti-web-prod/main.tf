# Create download.pariyatti.org S3 bucket
resource "aws_s3_bucket" "download_pariyatti_org_bucket" {
  bucket = "download.pariyatti.org" # Change to your desired bucket name
}

# Create an ACM certificate for download.pariyatti.org
resource "aws_acm_certificate" "download_pariyatti_org_certificate" {
  domain_name       = "download.pariyatti.org"
  validation_method = "DNS"

  tags = {
    Name = "download.pariyatti.org"
  }
}

# Create a CloudFront origin access identity
resource "aws_cloudfront_origin_access_identity" "download_pariyatti_org_oai" {
  comment = "download.pariyatti.org-oai"
}

# Attach bucket policy to allow access to the OAI
resource "aws_s3_bucket_policy" "download_pariyatti_org_bucket_policy" {
  bucket = aws_s3_bucket.download_pariyatti_org_bucket.id

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Id": "S3BucketPolicy",
  "Statement": [
    {
      "Sid": "AllowCloudFrontAccess",
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::cloudfront:user/CloudFront Origin Access Identity ${aws_cloudfront_origin_access_identity.download_pariyatti_org_oai.id}"
      },
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::download.pariyatti.org/*"
    }
  ]
}
EOF
}

# Create a CloudFront distribution
resource "aws_cloudfront_distribution" "download_pariyatti_org_distribution" {
  origin {
    domain_name = aws_s3_bucket.download_pariyatti_org_bucket.bucket_regional_domain_name
    origin_id   = "S3-download-pariyatti-bucket"
    s3_origin_config {
      origin_access_identity = aws_cloudfront_origin_access_identity.download_pariyatti_org_oai.cloudfront_access_identity_path
    }
  }

  enabled             = true
  is_ipv6_enabled     = true
  default_root_object = "index.html"

  default_cache_behavior {
    allowed_methods        = ["GET", "HEAD", "OPTIONS"]
    cached_methods         = ["GET", "HEAD"]
    target_origin_id       = "S3-download-pariyatti-bucket"
    viewer_protocol_policy = "redirect-to-https"  

    forwarded_values {
      query_string = false
      cookies {
        forward = "none"
      }
    }
  }

  # Switch to this once the certificate is provisoned
  viewer_certificate {
    acm_certificate_arn = aws_acm_certificate.download_pariyatti_org_certificate.arn
    ssl_support_method  = "sni-only"
  }

  aliases = ["download.pariyatti.org"]


  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  tags = {
    Name = "download-pariyatti-org-distribution"
  }
}