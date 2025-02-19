package io.github.alemazzo.squidcode.backend.submission.domain

import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

/**
 * Represents a candidate's submission for a coding problem.
 * This model captures the source code, chosen language, status, and evaluation details.
 *
 * @property id Unique identifier for the submission.
 * @property problemId The ID of the problem attempted.
 * @property userId (Optional) The candidate's user identifier.
 * @property code The submitted source code.
 * @property language The programming language of the submission.
 * @property status The current status of the submission.
 * @property result The aggregated result or feedback after evaluation.
 * @property submittedAt The timestamp when the submission was created.
 * @property evaluatedAt The timestamp when evaluation completed (if available).
 * @property executionTime Optional metric: execution time in milliseconds.
 * @property memoryUsage Optional metric: memory usage in kilobytes.
 */
@Introspected
data class Submission(
	val id: String,
	val problemId: String,
	val userId: String? = null,
	val code: String,
	val language: String,
	var status: SubmissionStatus = SubmissionStatus.QUEUED,
	var result: String? = null,
	val submittedAt: LocalDateTime = LocalDateTime.now(),
	var evaluatedAt: LocalDateTime? = null,
	var executionTime: Long? = null,
	var memoryUsage: Long? = null,
)

/**
 * Enumeration of possible submission statuses.
 */
enum class SubmissionStatus {
	QUEUED,
	RUNNING,
	COMPLETED,
	FAILED
}
