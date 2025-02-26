package io.github.alemazzo.squidcode.backend.users.infrastructure.persistence.mongo

import io.github.alemazzo.squidcode.backend.users.domain.User
import io.github.alemazzo.squidcode.backend.users.domain.UserRepository
import io.micronaut.context.annotation.Primary
import jakarta.inject.Singleton

@Singleton
@Primary
class UserMongoRepository(private val userMongoEntityRepository: UserMongoEntityRepository) : UserRepository {
	override fun findByGoogleId(googleId: String) = userMongoEntityRepository.findByGoogleId(googleId)?.toUser()
	override fun save(user: User) = userMongoEntityRepository.save(UserMongoEntity.fromUser(user)).toUser()
}