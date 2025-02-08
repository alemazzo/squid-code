package io.github.alemazzo.squidcode.backend.evaluation.domain

import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

/**
 * Represents an event in the lifecycle of a submission evaluation.
 * These events are used to notify real-time updates (e.g., via SSE/WebSocket).
 *
 * @property submissionId The ID of the related submission.
 * @property eventType The type of event that occurred.
 * @property message Optional message or additional data (e.g., evaluation result or error details).
 * @property timestamp When the event was created.
 */
@Introspected
data class EvaluationEvent(
	val submissionId: String,
	val eventType: EventType,
	val message: String? = null,
	val timestamp: LocalDateTime = LocalDateTime.now(),
)

/**
 * Defines the types of events that can occur during evaluation.
 */
enum class EventType {
	SUBMISSION_RECEIVED,
	EVALUATION_STARTED,
	EVALUATION_COMPLETED,
	EVALUATION_FAILED
}
