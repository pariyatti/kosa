output "healthcheck_id" {
  value       = aws_route53_health_check.kosa_healthcheck.id
  description = "ID of the Route53 healthcheck"
}
