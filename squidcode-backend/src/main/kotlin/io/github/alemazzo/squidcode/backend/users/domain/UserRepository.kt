package io.github.alemazzo.squidcode.backend.users.domain

interface UserRepository {
	fun findByGoogleId(googleId: String): User?
	fun save(user: User): User
}

