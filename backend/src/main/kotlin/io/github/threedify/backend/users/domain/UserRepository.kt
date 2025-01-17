package io.github.threedify.backend.users.domain

interface UserRepository {
	fun findByGoogleId(googleId: String): User?
	fun save(user: User): User
}

