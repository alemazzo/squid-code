package io.github.alemazzo.squidcode.backend.problems.application

import io.github.alemazzo.squidcode.backend.problems.domain.Problem
import io.github.alemazzo.squidcode.backend.problems.domain.ProblemRepository
import jakarta.inject.Singleton

@Singleton
class ProblemService(private val problemRepository: ProblemRepository) {
	fun getProblems(): List<ProblemDto> {
		return problemRepository.getProblems().map(Problem::toDto)
	}

	fun getProblemById(id: String): ProblemDto? {
		return problemRepository.getProblemById(id)?.toDto()
	}
}
