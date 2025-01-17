package io.github.threedify.backend.tasks.infrastructure.rabbit

import io.github.threedify.backend.tasks.application.TaskQueueProducer
import io.github.threedify.backend.tasks.domain.Task
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory.getLogger

@Singleton
class RabbitTaskQueueProducer(private val rabbitTaskQueueProducerClient: RabbitTaskQueueProducerClient) :
	TaskQueueProducer {
	private val logger = getLogger(this.javaClass)
	override fun enqueueTask(task: Task) {
		logger.info("Enqueuing tasks with id: ${task.id} and image: ${task.imageUrl}")
		rabbitTaskQueueProducerClient.enqueueTask(TaskMessage(task.id, task.imageUrl))
	}
}