# Create a new Pariyatti Lightsail Instance for sandbox-next
module "kosa-sandbox-next" {
  source = "../modules/kosa_server_lightsail"

  server_name = "kosa-sandbox-next"
  ports       = [80, 443, 22]

  server_blueprint_id = "ubuntu_22_04"

  # Manually set to 1 to update the txt files
  update_txt_files = 0
  server_tags = {
    env = "sandbox"
  }
  user_data = <<EOF
#! /bin/bash
echo "export BACKUP_S3_BUCKET=kosa-sandbox-next-data-backup" | sudo tee -a /root/.bashrc
echo "sandbox" | tee /tmp/kosa_env_name
EOF
}

module "sandbox-next-backup-bucket" {
  source = "../modules/backup_s3_bucket"

  bucket_name = "kosa-sandbox-next-data-backup"
  bucket_tags = {
    env     = "sandbox"
    purpose = "xtdb backup/static files backup"
  }
}

module "route53-healthcheck" {
  source = "../modules/kosa_monitoring"

  topic_name = "kosa-sandbox-next-healthcheck"
  server_url = "kosa-sandbox-next.pariyatti.app"
  server_tags = {
    env = "sandbox"
  }
}

output "instance_ip_addr" {
  value = module.kosa-sandbox-next.instance_ip_addr
}

output "instance_public_ip_addr" {
  value = module.kosa-sandbox-next.instance_public_ip_addr
}

output "instance_public_domain_name" {
  value = module.kosa-sandbox-next.instance_public_domain_name
}

output "backup_bucket_domain_name" {
  value = module.sandbox-next-backup-bucket.bucket_domain_name
}

output "backup_bucket_id" {
  value = module.sandbox-next-backup-bucket.bucket_id
}

output "backup_bucket_arn" {
  value = module.sandbox-next-backup-bucket.bucket_arn
}

output "route53_healthcheck_id" {
  value = module.route53-healthcheck.healthcheck_id
}
