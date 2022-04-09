# Create a new Pariyatti Lightsail Instance for production
module "kosa-production" {
  source = "../modules/kosa_server_lightsail"

  server_name = "kosa"
  ports       = [80, 443, 22]
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
