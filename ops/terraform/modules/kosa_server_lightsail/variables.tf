variable "server_name" {
  type        = string
  description = "The name for the server used for identifying on lightsail"
}

variable "server_availability_zone" {
  type        = string
  default     = "us-east-1b"
  description = "The data center where VM should be created"
}

variable "server_blueprint_id" {
  type        = string
  default     = "ubuntu_20_04"
  description = "The name of blueprint to use. That is, version and flavor of OS"
}

variable "server_size" {
  type        = string
  default     = "medium_2_0"
  description = "Size of the VM to be created. Refer to lightsail documentation for bundle ids"
}

variable "server_tags" {
  type        = map(string)
  default     = {}
  description = "Server tags that help in filtering resources while searching"
}

variable "ports" {
  type        = list(number)
  description = "The ingress TCP ports to open"
}

variable "user_data" {
  type        = string
  default     = ""
  description = "The user data to be passed to the VMs"
}
