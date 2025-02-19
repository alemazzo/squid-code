package io.github.alemazzo.squidcode.backend.problems.domain

/**
 * Represents a coding problem in the system.
 * Each problem contains a unique ID, title, description, difficulty level,
 * and a set of associated test cases.
 */
data class Problem(
	val id: String,
	val title: String,
	val description: String,
	val difficulty: Difficulty,
	val testCases: List<TestCase>,
	val tags: List<String> = emptyList(),
)

/**
 * Defines the difficulty level of a coding problem.
 */
enum class Difficulty {
	EASY,
	MEDIUM,
	HARD
}

/**
 * Represents a single test case for a coding problem.
 * Each test case contains an input and the corresponding expected output.
 */
data class TestCase(
	val input: String,
	val expectedOutput: String,
)