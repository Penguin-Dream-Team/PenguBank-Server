package club.pengubank.application

import club.pengubank.models.UserResponse
import club.pengubank.models.UserResponseWithJWT
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import java.util.*

object JWTAuthenticationConfig {

    private const val secret = "zAP5MBA4B4Ijz0MZaS48"
    const val issuer = "pengubank.club"
    private const val validityInMs = 36_000_00 * 1 // 1 hours
    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun makeToken(userResponse: UserResponse): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", userResponse.id)
        .withClaim("email", userResponse.email)
        .withClaim("registeredAt", userResponse.registeredAt)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
}

val ApplicationCall.user get() = authentication.principal<UserResponseWithJWT>()