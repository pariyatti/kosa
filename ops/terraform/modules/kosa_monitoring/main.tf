resource "aws_sns_topic" "kosa_route53_healthcheck" {
  name = var.topic_name
}

resource "aws_route53_health_check" "kosa_healthcheck" {
  fqdn              = var.server_url
  port              = 443
  type              = "HTTPS"
  resource_path     = "/status"
  failure_threshold = "3"
  request_interval  = "30"
  measure_latency   = "1"
  regions           = var.regions_list
  tags              = var.server_tags
}


resource "aws_cloudwatch_metric_alarm" "kosa_healthcheck_failed" {
  alarm_name          = format("%s_healthcheck_failed", var.server_url)
  namespace           = "AWS/Route53"
  metric_name         = "HealthCheckStatus"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = "1"
  period              = "60"
  statistic           = "Minimum"
  threshold           = "1"
  unit                = "None"
  dimensions = {
    HealthCheckId = aws_route53_health_check.kosa_healthcheck.id
  }
  alarm_description = "Monitor kosa backend health"
  alarm_actions     = [aws_sns_topic.kosa_route53_healthcheck.arn]
  ok_actions        = [aws_sns_topic.kosa_route53_healthcheck.arn]
}
