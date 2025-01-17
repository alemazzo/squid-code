package io.github.threedify.backend.users.domain

import java.util.*

data class User(
	val id: String,
	val googleId: String,
	val email: String,
) {
	init {
		require(id.isNotBlank()) { "id must not be blank" }
		require(googleId.isNotBlank()) { "googleId must not be blank" }
		require(email.isNotBlank()) { "email must not be blank" }
	}

	companion object {
		fun create(googleId: String, email: String): User {
			return User(
				id = UUID.randomUUID().toString(),
				googleId = googleId,
				email = email
			)
		}
	}
}