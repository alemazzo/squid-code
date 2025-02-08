package io.github.alemazzo.squidcode.backend.users.infrastructure.persistence.memory

import io.github.alemazzo.squidcode.backend.users.domain.User
import io.github.alemazzo.squidcode.backend.users.domain.UserRepository
import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

@Singleton
class InMemoryUserRepository : UserRepository {
	private val users = ConcurrentHashMap<String, User>()

	override fun findByGoogleId(googleId: String): User? {
		return users.values.find { it.googleId == googleId }
	}

	override fun save(user: User): User {
		users[user.id] = user
		return user
	}
}

