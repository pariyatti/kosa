variable "topic_name" {
  type        = string
  description = "SNS topic name for Route53 healthcheck"
}

variable "server_url" {
  type        = string
  description = "URL of the server to be monitored"
  default     = "kosa.pariyatti.app"
}

variable "regions_list" {
  type        = list(string)
  default     = ["eu-west-1", "us-east-1", "ap-southeast-1"]
  description = "List of regions to monitor from"

}
variable "server_tags" {
  type        = map(string)
  default     = {}
  description = "Server tags that help in filtering resources while searching"
}
