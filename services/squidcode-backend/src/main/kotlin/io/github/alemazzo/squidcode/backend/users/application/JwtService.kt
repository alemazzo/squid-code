package io.github.alemazzo.squidcode.backend.users.application

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.Date
import jakarta.inject.Singleton

@Singleton
class JwtService {
	private val secretKey =
		"df602d7f1e49e66fafb925f0b78a4ecab5c6c2e06834cf6496317eaab1862d24d4bb1342b2cd548704debd125794bbac8e7e8fcfad2a881be730f91399ae32e72df34ef89a53d4fa13910710a241893a7f0a19fdae662b5f040cd1aeb4dc91661cc177f4bef6bd37d386e9a4881caae51c1d585ce9bafb7f4c52944228effa75bc44db7c5fb45ac1e8253be353724fac22534ee91368dd151fd7a4efa679bfaf2e3cad24d687b82988ff3f3bf2c6fdbff8803d0d4a6373b9b5bfeaf308c5e667cffc3fc06088204b63a4c5e504d4e332ea74d74f351d26388aef79865f5a920d5cdb643f8d3a9b50ba4f0c1702ea0a2788bfee899be11cacaef66f1d14b7e1e4"
	private val expirationTimeInMs = 24 * 60 * 60 * 1000 // 1 day

	fun generateJwt(userId: String): String {
		val now = Date()
		val expiryDate = Date(now.time + expirationTimeInMs)

		return Jwts.builder()
			.setSubject(userId)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(SignatureAlgorithm.HS512, secretKey.toByteArray()) // Deprecated
			.compact()
	}

	fun validateJwt(token: String): Boolean {
		return try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey.toByteArray())
				.build()
				.parseClaimsJws(token)
			true
		} catch (ex: Exception) {
			false
		}
	}
}