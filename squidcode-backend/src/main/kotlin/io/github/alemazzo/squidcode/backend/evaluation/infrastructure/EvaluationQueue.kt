package io.github.alemazzo.squidcode.backend.evaluation.infrastructure

import io.github.alemazzo.squidcode.backend.submission.application.SubmissionDTO
import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentLinkedQueue

@Singleton
class EvaluationQueue {
	private val queue: ConcurrentLinkedQueue<SubmissionDTO> = ConcurrentLinkedQueue()

	fun enqueue(submission: SubmissionDTO) {
		queue.offer(submission)
	}

	fun dequeue(): SubmissionDTO? {
		return queue.poll()
	}
}
