# Create a new Pariyatti Lightsail Instance for sandbox
resource "aws_lightsail_instance" "kosa_sandbox" {
  name              = "kosa_sandbox"
  availability_zone = "us-east-1b"
  blueprint_id      = "ubuntu_20_04"
  bundle_id         = "micro_2_0"
  tags = {
    env = "sandbox"
  }
}

resource "aws_lightsail_instance_public_ports" "kosa_sandbox_https" {
  instance_name = aws_lightsail_instance.kosa_sandbox.name

  port_info {
    protocol  = "tcp"
    from_port = 443
    to_port   = 443
  }
  port_info {
    protocol  = "tcp"
    from_port = 80
    to_port   = 80
  }
  port_info {
    protocol  = "tcp"
    from_port = 22
    to_port   = 22
  }
}

resource "null_resource" "ansible_config" {

provisioner "local-exec" {
    command = "cd ../../ansible && ansible-playbook --become --limit 'kosa-sandbox.pariyatti.app' -i hosts provision.yml"
  }
}

resource "null_resource" "ansible_deploy" {

provisioner "local-exec" {
    command = "cd ../../ansible && ansible-playbook --become --limit 'kosa-sandbox.pariyatti.app' -i hosts deploy.yml"
  }
}

# Update DNS pointer for kosa-sandbox.pariyatti.app

data "aws_route53_zone" "app_domain" {
  name         = "pariyatti.app"
}

resource "aws_route53_record" "kosa_sandbox_dns_record" {
  zone_id = data.aws_route53_zone.app_domain.zone_id
  name    = "kosa-sandbox.${data.aws_route53_zone.app_domain.name}"
  type    = "A"
  ttl     = "300"
  records = [aws_lightsail_instance.kosa_sandbox.public_ip_address]
}

output "instance_ip_addr" {
  value = aws_lightsail_instance.kosa_sandbox.private_ip_address
}

output "instance_public_ip_addr" {
  value = aws_lightsail_instance.kosa_sandbox.public_ip_address
}

output "instance_public_domain_name" {
  value = aws_route53_record.kosa_sandbox_dns_record.name
}

