package application

import models.SimpleUserResponse
import models.UserResponseWith2FAJWT
import models.UserResponseWithJWT
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

    fun makeToken(userResponse: SimpleUserResponse): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim(JWTClaims.ID, userResponse.id)
        .withClaim(JWTClaims.EMAIL, userResponse.email)
        .withClaim(JWTClaims.REGISTERED_AT, userResponse.registeredAt)
        .withClaim(JWTClaims.ENABLED_2FA, userResponse.enabled2FA)
        .withClaim(JWTClaims.ACCOUNT_ID, userResponse.accountId)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    fun make2FAToken(userResponse: SimpleUserResponse): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim(JWTClaims.ID, userResponse.id)
        .withClaim(JWTClaims.EMAIL, userResponse.email)
        .withClaim(JWTClaims.REGISTERED_AT, userResponse.registeredAt)
        .withClaim(JWTClaims.ENABLED_2FA, userResponse.enabled2FA)
        .withClaim(JWTClaims.ACCOUNT_ID, userResponse.accountId)
        .withClaim(JWTClaims.VERIFIED, true)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
}

val ApplicationCall.tempUser get() = authentication.principal<UserResponseWithJWT>()
val ApplicationCall.user get() = authentication.principal<UserResponseWith2FAJWT>()
val ApplicationCall.guest get() = request.headers["Authorization"].isNullOrBlank()

object JWTClaims {
    const val ID = "id"
    const val EMAIL = "email"
    const val REGISTERED_AT = "registeredAt"
    const val ENABLED_2FA = "enabled2FA"
    const val ACCOUNT_ID = "accountId"
    const val VERIFIED = "verified"
}
