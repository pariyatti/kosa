# Create a new Pariyatti Lightsail Instance for production
module "kosa-production" {
  source = "../modules/kosa_server_lightsail"

  server_name = "kosa"
  ports       = [80, 443, 22]
  server_tags = {
    env = "production"
  }
  user_data = <<EOF
#! /bin/bash
echo "export BACKUP_S3_BUCKET=kosa-production-data-backup" | sudo tee -a /root/.bashrc
echo "production" | tee /tmp/kosa_env_name
EOF
}

module "production-backup-bucket" {
  source = "../modules/backup_s3_bucket"

  bucket_name = "kosa-production-data-backup"
  bucket_tags = {
    env     = "production"
    purpose = "xtdb backup/static files backup"
  }
}

module "route53-healthcheck" {
  source = "../modules/kosa_monitoring"

  topic_name = "kosa-production-healthcheck"
  server_url = "kosa.pariyatti.app"
  server_tags = {
    env = "production"
  }
}

output "instance_ip_addr" {
  value = module.kosa-production.instance_ip_addr
}

output "instance_public_ip_addr" {
  value = module.kosa-production.instance_public_ip_addr
}

output "instance_public_domain_name" {
  value = module.kosa-production.instance_public_domain_name
}

output "backup_bucket_domain_name" {
  value = module.production-backup-bucket.bucket_domain_name
}

output "backup_bucket_id" {
  value = module.production-backup-bucket.bucket_id
}

output "backup_bucket_arn" {
  value = module.production-backup-bucket.bucket_arn
}
