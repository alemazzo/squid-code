package io.github.alemazzo.squidcode.backend.evaluation

import io.github.alemazzo.squidcode.backend.evaluation.infrastructure.EvaluationQueue
import io.github.alemazzo.squidcode.backend.submission.application.SubmissionDTO
import io.github.alemazzo.squidcode.backend.submission.domain.Submission
import io.github.alemazzo.squidcode.backend.submission.domain.SubmissionRepository
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton

@Singleton
class EvaluationWorker(
	private val repository: SubmissionRepository,
	private val queue: EvaluationQueue,
	private val eventPublisher: EventPublisher,
) {

	@Scheduled(fixedDelay = "10s")
	fun processQueue() {
		var submission: SubmissionDTO? = queue.dequeue()
		while (submission != null) {
			submission.status = "running"
			repository.update(submission)
			eventPublisher.publish(
				io.github.alemazzo.squidcode.backend.evaluation.domain.EvaluationEvent(
					submission.id,
					"EVALUATION_STARTED"
				)
			)
			// Simulate evaluation: in a real system, run the code in a sandbox
			val result = evaluate(submission)
			submission.status = "completed"
			submission.result = result
			repository.update(submission)
			eventPublisher.publish(
				io.github.alemazzo.squidcode.backend.evaluation.domain.EvaluationEvent(
					submission.id,
					"EVALUATION_COMPLETED",
					result
				)
			)
			submission = queue.dequeue()
		}
	}

	private fun evaluate(submission: Submission): String {
		// Dummy evaluation logic
		return "All test cases passed"
	}
}
