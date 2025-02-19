package io.github.alemazzo.squidcode.backend.submission.application

import io.github.alemazzo.squidcode.backend.submission.domain.Submission
import io.github.alemazzo.squidcode.backend.submission.domain.SubmissionRepository
import io.github.alemazzo.squidcode.backend.submission.domain.SubmissionStatus
import io.github.alemazzo.squidcode.backend.submission.interfaces.SubmissionRequest
import jakarta.inject.Singleton
import java.util.*

@Singleton
class SubmissionService(
	private val repository: SubmissionRepository,
) {

	fun submitSolution(request: SubmissionRequest): String {
		val submission = Submission(
			id = UUID.randomUUID().toString(),
			problemId = request.problemId,
			code = request.code,
			language = request.language,
			status = SubmissionStatus.QUEUED
		)
		repository.save(submission)
		return submission.id
	}

	fun getSubmission(submissionId: String): SubmissionDTO? {
		return repository.findById(submissionId)?.toDTO()
	}
}
