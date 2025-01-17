package io.github.threedify.backend.tasks.application

import io.github.threedify.backend.tasks.domain.Task
import io.github.threedify.backend.tasks.domain.TaskRepository
import io.github.threedify.backend.tasks.domain.TaskStatus
import jakarta.inject.Singleton

@Singleton
class TaskService(
	private val taskRepository: TaskRepository,
	private val taskQueueProducer: TaskQueueProducer,
) {
	fun createTask(image: String): Task {
		val task = Task(
			imageUrl = image,
			status = TaskStatus.PENDING
		)
		val savedTask = taskRepository.save(task)
		taskQueueProducer.enqueueTask(savedTask)
		return savedTask
	}

	fun getTask(id: String): Task? {
		return taskRepository.findById(id)
	}

	fun start(id: String): Task {
		val task = taskRepository.findById(id) ?: throw TaskNotFoundException(id)
		val updatedTask = task.copy(status = TaskStatus.STARTED)
		return taskRepository.update(updatedTask)
	}

	fun fail(id: String): Task {
		val task = taskRepository.findById(id) ?: throw TaskNotFoundException(id)
		val updatedTask = task.copy(status = TaskStatus.FAILED)
		return taskRepository.update(updatedTask)
	}

	fun completeTask(id: String, resultUrl: String): Task {
		val task = taskRepository.findById(id) ?: throw TaskNotFoundException(id)
		val updatedTask = task.copy(resultUrl = resultUrl, status = TaskStatus.COMPLETED)
		return taskRepository.update(updatedTask)
	}
}

