package io.github.alemazzo.squidcode.backend.submission.application

import io.github.alemazzo.squidcode.backend.submission.domain.Submission

data class SubmissionDTO(
	val submissionId: String,
	val status: String,
	val result: String? = null,
)

fun Submission.toDTO() = SubmissionDTO(id, status.name, result)
