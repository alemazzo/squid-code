package io.github.alemazzo.squidcode.backend.evaluation

import io.github.alemazzo.squidcode.backend.evaluation.domain.EvaluationEvent
import io.micronaut.context.event.ApplicationEventPublisher
import jakarta.inject.Singleton

@Singleton
class EventPublisher(private val publisher: ApplicationEventPublisher<EvaluationEvent>) {
	fun publish(event: EvaluationEvent) {
		publisher.publishEvent(event)
	}
}

data class EvaluationEvent(
	val submissionId: String,
	val eventType: String,
	val result: String? = null,
)
