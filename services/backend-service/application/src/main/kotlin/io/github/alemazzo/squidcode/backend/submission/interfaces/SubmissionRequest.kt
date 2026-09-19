package io.github.alemazzo.squidcode.backend.submission.interfaces

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class SubmissionRequest(
	val problemId: String,
	val code: String,
	val language: String,
)

@Serdeable
data class SubmissionResponse(
	val submissionId: String,
	val status: String,
)


