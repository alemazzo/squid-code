package io.github.threedify.backend.tasks.controllers

import io.github.threedify.backend.tasks.application.TaskService
import io.github.threedify.backend.tasks.domain.Task
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.serde.annotation.Serdeable
import org.slf4j.LoggerFactory.getLogger

@Controller("/tasks")
class TaskController(private val taskService: TaskService) {

	private val logger = getLogger(this.javaClass)

	@Serdeable
	data class TaskResponse(
		val id: String,
		val status: String,
	)

	@Serdeable
	data class TaskRequest(
		val image: String,
	)

	/**
	 * Endpoint to create a tasks by uploading an image.
	 */
	@Post
	fun createTask(@Body request: TaskRequest): HttpResponse<TaskResponse> {
		logger.info("Creating tasks for image: ${request.image}")
		val task = taskService.createTask(request.image)
		return HttpResponse.created(TaskResponse(task.id, task.status.name))
	}

	/**
	 * Endpoint to fetch a tasks by its ID.
	 */
	@Get("/{id}")
	fun getTask(@PathVariable id: String): HttpResponse<Task> {
		logger.info("Fetching tasks with ID: $id")
		val task = taskService.getTask(id)
		return if (task != null) {
			HttpResponse.ok(task)
		} else {
			HttpResponse.notFound()
		}
	}
}
