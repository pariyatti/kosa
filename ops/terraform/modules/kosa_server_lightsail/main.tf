# Create a new Pariyatti Lightsail Instance for sandbox
resource "aws_lightsail_instance" "kosa_server" {
  name              = var.server_name
  availability_zone = var.server_availability_zone
  blueprint_id      = var.server_blueprint_id
  bundle_id         = var.server_size
  tags              = var.server_tags
}

resource "aws_lightsail_instance_public_ports" "kosa_server_ports" {
  instance_name = aws_lightsail_instance.kosa_server.name

  dynamic "port_info" {
    for_each = var.ports
    content {
      protocol = "tcp"
      cidrs = [
        "0.0.0.0/0",
      ]
      from_port = port_info.value
      to_port   = port_info.value
    }
  }
}

resource "null_resource" "ansible_config" {
  triggers = {
    cluster_instance_ids = aws_lightsail_instance.kosa_server.arn
  }

  provisioner "local-exec" {
    command = "cd ../../ansible && ansible-playbook --become --limit ${var.server_name}.pariyatti.app -i hosts provision.yml"
  }
}

resource "null_resource" "ansible_deploy" {
  depends_on = [
    null_resource.ansible_config
  ]
  triggers = {
    cluster_instance_ids = aws_lightsail_instance.kosa_server.arn
    build_number         = "${timestamp()}"
  }

  provisioner "local-exec" {
    command = "cd ../../ansible && ansible-playbook --become --limit ${var.server_name}.pariyatti.app -i hosts deploy.yml"
  }
}

resource "null_resource" "ansible_seed_data" {
  depends_on = [
    null_resource.ansible_config,
    null_resource.ansible_deploy
  ]
  triggers = {
    cluster_instance_ids = aws_lightsail_instance.kosa_server.arn
  }

  provisioner "local-exec" {
    command = "cd ../../ansible && ansible-playbook --become --limit ${var.server_name}.pariyatti.app -i hosts seed_looped_txt.yml"
  }
}

# Update DNS pointer for kosa-server to <name>.pariyatti.app

data "aws_route53_zone" "app_domain" {
  name = "pariyatti.app"
}

resource "aws_route53_record" "kosa_server_dns_record" {
  zone_id = data.aws_route53_zone.app_domain.zone_id
  name    = "${var.server_name}.${data.aws_route53_zone.app_domain.name}"
  type    = "A"
  ttl     = "300"
  records = [aws_lightsail_instance.kosa_server.public_ip_address]
}


