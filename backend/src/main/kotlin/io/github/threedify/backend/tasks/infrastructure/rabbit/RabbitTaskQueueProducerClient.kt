package io.github.threedify.backend.tasks.infrastructure.rabbit

import io.micronaut.rabbitmq.annotation.Binding
import io.micronaut.rabbitmq.annotation.RabbitClient
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class TaskMessage(val id: String, val image: String)

@RabbitClient("threedify")
interface RabbitTaskQueueProducerClient {
	@Binding("tasks")
	fun enqueueTask(taskMessage: TaskMessage)
}

