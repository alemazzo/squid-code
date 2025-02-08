package io.github.alemazzo.squidcode.backend.users.infrastructure.persistence.mongo

import io.github.alemazzo.squidcode.backend.users.domain.User
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.mongodb.annotation.MongoRepository
import io.micronaut.data.repository.CrudRepository


@MappedEntity
class UserMongoEntity(
	@field:Id
	var id: String = "",
	var googleId: String = "",
	var email: String = "",
) {
	fun toUser(): User {
		return User(
			id = id,
			googleId = googleId,
			email = email
		)
	}

	companion object {
		fun fromUser(user: User): UserMongoEntity {
			return UserMongoEntity(
				id = user.id,
				googleId = user.googleId,
				email = user.email
			)
		}
	}
}


@MongoRepository
interface UserMongoEntityRepository : CrudRepository<UserMongoEntity, String> {
	fun findByGoogleId(googleId: String): UserMongoEntity?
}