package io.github.threedify.backend.users.application

import io.github.threedify.backend.users.domain.AuthUserInfo

interface AuthProvider {
	fun getUserInfo(token: String): AuthUserInfo
}

