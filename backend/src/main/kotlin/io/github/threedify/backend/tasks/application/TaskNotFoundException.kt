package io.github.threedify.backend.tasks.application

class TaskNotFoundException(id: String) : RuntimeException("Task with id $id not found")