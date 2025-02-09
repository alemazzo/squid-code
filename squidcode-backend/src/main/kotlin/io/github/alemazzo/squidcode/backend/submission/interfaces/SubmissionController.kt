package io.github.alemazzo.squidcode.backend.submission.interfaces

import io.github.alemazzo.squidcode.backend.submission.application.SubmissionDTO
import io.github.alemazzo.squidcode.backend.submission.application.SubmissionService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post

@Controller("/submissions")
class SubmissionController(private val submissionService: SubmissionService) {

	@Post("/")
	fun submit(@Body submissionRequest: SubmissionRequest): HttpResponse<SubmissionResponse> {
		val submissionId = submissionService.submitSolution(submissionRequest)
		return HttpResponse.created(SubmissionResponse(submissionId, "queued"))
	}

	@Get("/{submissionId}")
	fun getStatus(submissionId: String): HttpResponse<SubmissionDTO> {
		val submission = submissionService.getSubmission(submissionId)
		return if (submission != null) HttpResponse.ok(submission) else HttpResponse.notFound()
	}
}
