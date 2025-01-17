package io.github.threedify.backend.tasks.domain

interface TaskRepository {
	fun save(task: Task): Task
	fun findById(id: String): Task?
	fun update(task: Task): Task
}