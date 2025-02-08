terraform {
  required_providers {
    docker = {
      source = "kreuzwerker/docker"
      version = "~> 3.0.1"
    }
  }
}

provider "docker" {}

resource "docker_network" "squidcode_network" {
  name = "squidcode-network"
}

resource "docker_container" "mongodb" {
  name  = "mongodb"
  image = "mongo:latest"
  restart = "always"
  ports {
    internal = 27017
    external = 27017
  }
  networks_advanced {
    name = docker_network.squidcode_network.name
  }
}

resource "docker_container" "squidcode_backend" {
  name  = "squidcode-backend"
  image = "alessandromazzoli/squidcode-backend:latest"
  restart = "always"
  depends_on = [docker_container.mongodb, docker_container.rabbitmq]
  networks_advanced {
    name = docker_network.squidcode_network.name
  }
}
