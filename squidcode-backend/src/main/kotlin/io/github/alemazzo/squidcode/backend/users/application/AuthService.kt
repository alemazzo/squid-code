package io.github.alemazzo.squidcode.backend.users.application

import io.github.alemazzo.squidcode.backend.users.domain.User
import io.github.alemazzo.squidcode.backend.users.domain.UserRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory.getLogger

@Singleton
class AuthService(
	private val authProvider: AuthProvider,
	private val jwtService: JwtService,
	private val userRepository: UserRepository,
) {
	private val logger = getLogger(this.javaClass)

	fun authenticate(token: String): String {
		logger.info("Authenticating user")
		val userInfo = authProvider.getUserInfo(token)
		logger.info("User info: $userInfo")

		if (userRepository.findByGoogleId(userInfo.id) != null) {
			logger.info("User exists")
		} else {
			logger.info("User does not exist")
		}

		val user = userRepository.findByGoogleId(userInfo.id) ?: userRepository.save(
			User.create(googleId = userInfo.id, email = userInfo.email)
		)
		logger.info("User: $user")
		return jwtService.generateJwt(user.id)
	}
}