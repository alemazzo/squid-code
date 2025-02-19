package io.github.alemazzo.squidcode.backend.problems.domain

import jakarta.inject.Singleton

@Singleton
class ProblemRepository {
	private val problems = mutableListOf<Problem>()

	fun addProblem(problem: Problem) {
		problems.add(problem)
	}

	fun getProblems(): List<Problem> {
		return problems
	}

	fun getProblemById(id: String): Problem? {
		return problems.find { it.id == id }
	}
}