output "instance_ip_addr" {
  value = aws_lightsail_instance.kosa_server.private_ip_address
}

output "instance_public_ip_addr" {
  value = aws_lightsail_instance.kosa_server.public_ip_address
}

output "instance_public_domain_name" {
  value = aws_route53_record.kosa_server_dns_record.name
}
