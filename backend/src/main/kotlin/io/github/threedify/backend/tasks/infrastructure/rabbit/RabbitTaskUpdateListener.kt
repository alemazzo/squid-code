package io.github.threedify.backend.tasks.infrastructure.rabbit

import io.github.threedify.backend.tasks.application.TaskUpdateListener
import io.micronaut.rabbitmq.annotation.Queue
import io.micronaut.rabbitmq.annotation.RabbitListener
import io.micronaut.serde.annotation.Serdeable
import org.slf4j.LoggerFactory.getLogger

@RabbitListener
class RabbitTaskUpdateListener(
	private val taskUpdateListener: TaskUpdateListener,
) {

	private val logger = getLogger(this.javaClass)

	@Serdeable
	data class TaskStartedEvent(
		val id: String,
	)

	@Queue("starts")
	fun onTaskStarted(taskStartedEvent: TaskStartedEvent) {
		logger.info("Received start event for tasks ${taskStartedEvent.id}")
		taskUpdateListener.onTaskStarted(taskStartedEvent.id)
	}

	@Serdeable
	data class TaskCompletedEvent(
		val id: String,
		val result: String,
	)

	@Queue("results")
	fun onTaskCompleted(taskCompletedEvent: TaskCompletedEvent) {
		logger.info("Received result for tasks ${taskCompletedEvent.id}: ${taskCompletedEvent.result}")
		taskUpdateListener.onTaskCompleted(taskCompletedEvent.id, taskCompletedEvent.result)
	}
}