package io.github.threedify.backend.users.controllers

import io.github.threedify.backend.users.application.AuthService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.serde.annotation.Serdeable
import org.slf4j.LoggerFactory.getLogger

@Controller("/auth")
class AuthController(private val authService: AuthService) {

	private val logger = getLogger(this.javaClass)

	@Serdeable
	data class AuthRequest(val token: String)

	@Post("/login")
	fun login(@Body request: AuthRequest): HttpResponse<String> {
		logger.info("Logging in")
		val jwt = authService.authenticate(request.token)
		logger.info("JWT: $jwt")
		return HttpResponse.ok(jwt)
	}
}