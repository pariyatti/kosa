# Create a new Pariyatti Lightsail Instance for sandbox
module "kosa-sandbox" {
  source = "../modules/kosa_server_lightsail"

  server_name = "kosa-sandbox"
  ports       = [80, 443, 22]
  server_tags = {
    env = "sandbox"
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

