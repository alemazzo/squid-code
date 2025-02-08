package io.github.alemazzo.squidcode.backend.submission.interfaces

data class SubmissionRequest(
	val problemId: String,
	val code: String,
	val language: String,
)

data class SubmissionResponse(
	val submissionId: String,
	val status: String,
)


