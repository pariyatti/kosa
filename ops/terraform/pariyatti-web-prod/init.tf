terraform {
  required_version = ">= 1.4.5"
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }
  backend "s3" {
    bucket = "pariyatti-tf-state-bucket"
    key    = "pariyatti-web-prod/terraform.tfstate"
    region = "us-east-1"
  }
}

# Configure the AWS Provider
provider "aws" {
  region = "us-east-1"
}