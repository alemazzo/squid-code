package io.github.alemazzo.squidcode.backend.problems.application

import io.github.alemazzo.squidcode.backend.problems.domain.Problem

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class ProblemDto(
	val id: String,
	val title: String,
	val difficulty: String,
	val description: String,
)

fun Problem.toDto() = ProblemDto(id, title, difficulty.name, description)
