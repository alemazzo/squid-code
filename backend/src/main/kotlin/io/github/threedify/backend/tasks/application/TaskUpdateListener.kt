package io.github.threedify.backend.tasks.application

import jakarta.inject.Singleton

@Singleton
class TaskUpdateListener(
	private val taskService: TaskService,
) {
	fun onTaskStarted(id: String) {
		taskService.start(id)
	}

	fun onTaskCompleted(id: String, result: String) {
		taskService.completeTask(id, result)
	}
}