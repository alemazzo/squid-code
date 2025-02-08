variable "mongodb_image" {
  default = "mongo:latest"
}

variable "squidcode_backend_image" {
  default = "alessandromazzoli/squidcode-backend:latest"
}

variable "docker_network" {
  default = "squidcode-network"
}
