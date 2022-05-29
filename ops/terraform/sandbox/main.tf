# Create a new Pariyatti Lightsail Instance for sandbox
module "kosa-sandbox" {
  source = "../modules/kosa_server_lightsail"

  server_name = "kosa-sandbox"
  ports       = [80, 443, 22]
  server_tags = {
    env = "sandbox"
  }
}

module "sandbox-backup-bucket" {
  source = "../modules/backup_s3_bucket"

  bucket_name = "kosa-sandbox-data-backup"
  bucket_tags = {
    env = "sandbox"
    purpose = "xtdb backup/static files backup"
  }
}

output "instance_ip_addr" {
  value = module.kosa-sandbox.instance_ip_addr
}

output "instance_public_ip_addr" {
  value = module.kosa-sandbox.instance_public_ip_addr
}

output "instance_public_domain_name" {
  value = module.kosa-sandbox.instance_public_domain_name
}

output "backup_bucket_domain_name" {
  value = module.sandbox-backup-bucket.bucket_domain_name
}

output "backup_bucket_id" {
  value = module.sandbox-backup-bucket.bucket_id
}

output "backup_bucket_arn" {
  value = module.sandbox-backup-bucket.bucket_arn
}

