package io.github.threedify.backend.users.infrastructure.auth

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import io.github.threedify.backend.users.application.AuthProvider
import io.github.threedify.backend.users.domain.AuthUserInfo
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory.getLogger
import java.util.*

@Singleton
class GoogleAuthProvider(
	@Value("\${google.client-id}") private val clientId: String,
) : AuthProvider {
	private val logger = getLogger(this.javaClass)
	private val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
	private val transport = GoogleNetHttpTransport.newTrustedTransport()

	private val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
		.setAudience(Collections.singletonList(clientId))
		.build()

	override fun getUserInfo(token: String): AuthUserInfo {
		logger.info("Getting user info")
		val idToken: GoogleIdToken = verifier.verify(token)
			?: throw IllegalArgumentException("Invalid Google token")
		logger.info("ID token: $idToken")
		val payload = idToken.payload
		return AuthUserInfo(
			id = payload.subject,
			email = payload.email,
		)
	}

}