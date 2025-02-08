package io.github.alemazzo.squidcode.backend.users.application

import io.github.alemazzo.squidcode.backend.users.domain.AuthUserInfo

interface AuthProvider {
	fun getUserInfo(token: String): AuthUserInfo
}

