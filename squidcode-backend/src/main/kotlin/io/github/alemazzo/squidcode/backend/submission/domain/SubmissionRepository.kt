package io.github.alemazzo.squidcode.backend.submission.domain

import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

@Singleton
class SubmissionRepository {
	private val submissions = ConcurrentHashMap<String, Submission>()

	fun save(submission: Submission) {
		submissions[submission.id] = submission
	}

	fun update(submission: Submission) {
		submissions[submission.id] = submission
	}

	fun findById(id: String): Submission? = submissions[id]

	fun findQueuedSubmissions(): List<Submission> =
		submissions.values.filter { it.status == SubmissionStatus.QUEUED }
}
