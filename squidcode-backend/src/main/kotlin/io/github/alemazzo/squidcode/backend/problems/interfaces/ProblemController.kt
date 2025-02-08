package io.github.alemazzo.squidcode.backend.problems.interfaces

import io.github.alemazzo.squidcode.backend.problems.application.ProblemDto
import io.github.alemazzo.squidcode.backend.problems.application.ProblemService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller("/api/problems")
class ProblemController(private val problemService: ProblemService) {

	@Get("/")
	fun listProblems(): HttpResponse<List<ProblemDto>> {
		val problems = problemService.getProblems()
		return HttpResponse.ok(problems)
	}

	@Get("/{id}")
	fun getProblem(id: String): HttpResponse<ProblemDto> {
		val problem = problemService.getProblemById(id)
		return if (problem != null) HttpResponse.ok(problem) else HttpResponse.notFound()
	}
}
