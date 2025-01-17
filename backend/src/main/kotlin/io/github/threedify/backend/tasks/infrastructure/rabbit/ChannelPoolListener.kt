package io.github.threedify.backend.tasks.infrastructure.rabbit

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import io.micronaut.rabbitmq.connect.ChannelInitializer
import jakarta.inject.Singleton
import java.io.IOException


@Singleton
class ChannelPoolListener : ChannelInitializer() {

	@Throws(IOException::class)
	override fun initialize(channel: Channel, name: String) {
		channel.exchangeDeclare("threedify", BuiltinExchangeType.DIRECT, true)
		channel.queueDeclare("tasks", true, false, false, null)
		channel.queueBind("tasks", "threedify", "tasks")
		channel.queueDeclare("results", true, false, false, null)
		channel.queueBind("results", "threedify", "results")
		channel.queueDeclare("starts", true, false, false, null)
		channel.queueBind("starts", "threedify", "starts")
	}
}