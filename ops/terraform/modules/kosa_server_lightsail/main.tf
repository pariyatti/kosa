# Create a new Pariyatti Lightsail Instance for sandbox
resource "aws_lightsail_instance" "kosa_server" {
  name              = var.server_name
  availability_zone = var.server_availability_zone
  blueprint_id      = var.server_blueprint_id
  bundle_id         = var.server_size
  tags              = var.server_tags
  user_data         = var.user_data
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
  depends_on = [
    aws_route53_record.kosa_server_dns_record,
    time_sleep.wait_60_seconds
  ]
  triggers = {
    cluster_instance_ids = aws_lightsail_instance.kosa_server.arn
    file_checksum        = filemd5("../../ansible/provision.yml")
  }

  provisioner "local-exec" {
    command = "cd ../../ansible && ansible-playbook --become --extra-vars @secrets.yml --vault-password-file ~/.kosa/ansible-password --limit ${var.server_name}.pariyatti.app -i hosts provision.yml"
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
    command = "cd ../../ansible && ansible-playbook --extra-vars @secrets.yml --vault-password-file ~/.kosa/ansible-password --limit ${var.server_name}.pariyatti.app -i hosts deploy.yml"
  }
}

# This resource doesn't need to run each time deployment happens.
# It helps when we are trying to repopulate database for maintenance purposes only
# So say you want to actually do it, taint the following two resources manually
# terraform taint module.kosa-<env>.null_resource.ansible_trunc_data
# terraform taint module.kosa-<env>.null_resource.ansible_seed_data
resource "null_resource" "ansible_trunc_data" {
  depends_on = [
    null_resource.ansible_config,
    null_resource.ansible_deploy
  ]
  triggers = {
    cluster_instance_ids = aws_lightsail_instance.kosa_server.arn
  }

  provisioner "local-exec" {
    command = "cd ../../ansible && ansible-playbook --extra-vars @secrets.yml --vault-password-file ~/.kosa/ansible-password --limit ${var.server_name}.pariyatti.app -i hosts db_txt_trunc.yml"
  }
}

resource "null_resource" "ansible_seed_data" {
  depends_on = [
    null_resource.ansible_config,
    null_resource.ansible_deploy,
    null_resource.ansible_trunc_data
  ]
  triggers = {
    cluster_instance_ids = aws_lightsail_instance.kosa_server.arn
  }

  provisioner "local-exec" {
    command = "cd ../../ansible && ansible-playbook --extra-vars @secrets.yml --vault-password-file ~/.kosa/ansible-password --limit ${var.server_name}.pariyatti.app -i hosts seed_looped_txt.yml"
  }
}

# reseed_txt_files
resource "null_resource" "ansible_reseed_txt_files" {
  depends_on = [
    null_resource.ansible_config,
    null_resource.ansible_deploy
  ]
  count = var.reseed_txt_files && !var.update_txt_files ? 1 : 0

  provisioner "local-exec" {
    command = "cd ../../ansible && ansible-playbook --extra-vars @secrets.yml --vault-password-file ~/.kosa/ansible-password --limit ${var.server_name}.pariyatti.app -i hosts reseed_looped_txt.yml"
  }
}

resource "null_resource" "ansible_update_data" {
  depends_on = [
    null_resource.ansible_config,
    null_resource.ansible_deploy
  ]
  count = var.update_txt_files && !var.reseed_txt_files ? 1:0

  provisioner "local-exec" {
    command = "cd ../../ansible && ansible-playbook --extra-vars @secrets.yml --vault-password-file ~/.kosa/ansible-password --limit ${var.server_name}.pariyatti.app -i hosts update_looped_txt.yml"
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
  ttl     = "30"
  records = [aws_lightsail_instance.kosa_server.public_ip_address]
}

resource "null_resource" "lightsail_python_scripts" {
  depends_on = [
    null_resource.ansible_seed_data
  ]
  triggers = {
    cluster_instance_ids = aws_lightsail_instance.kosa_server.arn
  }

  provisioner "local-exec" {
    command = "cd ../../scripts/aws_lightsail && ./run_lightsail_scripts.sh"
  }
}

resource "time_sleep" "wait_60_seconds" {
  depends_on = [
    aws_route53_record.kosa_server_dns_record
  ]

  create_duration = "60s"
}
