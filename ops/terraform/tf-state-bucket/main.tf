terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
    }
  }
}

# Configure the AWS Provider
provider "aws" {
  region = "us-east-1"
}

# One time creation in an account. Please do not run terraform delete in this folder
resource "aws_s3_bucket" "tf-state-bucket" {
  bucket = "pariyatti-tf-state-bucket"

  tags = {
    Name        = "pariyatti-tf-state-bucket"
    env = "production"
  }
}

resource "aws_s3_bucket_acl" "tf-state-bucket" {
  bucket = aws_s3_bucket.tf-state-bucket.id
  acl    = "private"
}