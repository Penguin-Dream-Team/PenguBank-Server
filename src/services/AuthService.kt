package services

import application.JWTClaims
import controllers.RegisterRequest
import controllers.Verify2FARequest
import responses.exceptions.user.UserInvalid2FACodeException
import responses.exceptions.user.UserRegistrationDuplicateEmailException
import responses.exceptions.user.UserRegistrationPasswordsDoNotMatch
import responses.exceptions.user.UserWrongCredentialsException
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import models.SimpleUserResponse
import models.User
import models.UserResponseWith2FAJWT
import models.UserResponseWithJWT
import org.springframework.security.crypto.bcrypt.BCrypt
import repositories.UserRepository
import totp.TOTPAuthenticator

class AuthService(private val userRepository: UserRepository) {

    fun login(email: String, password: String): User {
        val user = userRepository.getUser(email)

        return if (BCrypt.checkpw(
                password,
                user.password
            )
        ) user else throw UserWrongCredentialsException()
    }

    fun register(registerValues: RegisterRequest): User {
        if (userRepository.hasUser(registerValues.email)) throw UserRegistrationDuplicateEmailException(registerValues.email)
        if (registerValues.password != registerValues.confirmPassword) throw UserRegistrationPasswordsDoNotMatch()

        val cipheredPassword = BCrypt.hashpw(registerValues.password, BCrypt.gensalt())

        val secretKey = TOTPAuthenticator().createSecretKey()

        return userRepository.addUser(
            User(
                email = registerValues.email,
                password = cipheredPassword,
                secretKey = secretKey.toString()
            )
        )
    }

    fun verify2FA(userId: Int, verifyValues: Verify2FARequest) {
        verifyValues.code ?: throw UserInvalid2FACodeException()
        val secretKey = userRepository.getUser(userId).getSecretKey()

        if (!TOTPAuthenticator().authorize(secretKey, verifyValues.code))
            throw UserInvalid2FACodeException()
    }

    fun validateUserJWT(credential: JWTCredential): Principal? {
        return UserResponseWithJWT(getUserClaims(credential))
    }

    fun validateUser2FAJWT(credential: JWTCredential): Principal? {
        val verified = credential.payload.getClaim(JWTClaims.VERIFIED).asBoolean()
        return UserResponseWith2FAJWT(getUserClaims(credential), verified)
    }

    private fun getUserClaims(credential: JWTCredential): SimpleUserResponse {
        val id = credential.payload.getClaim(JWTClaims.ID).asInt()
        val email = credential.payload.getClaim(JWTClaims.EMAIL).asString()
        val registeredAt = credential.payload.getClaim(JWTClaims.REGISTERED_AT).asString()
        val enabled2FA = credential.payload.getClaim(JWTClaims.ENABLED_2FA).asBoolean()
        val accountId = credential.payload.getClaim(JWTClaims.ACCOUNT_ID).asInt()

        return SimpleUserResponse(id, email, registeredAt, enabled2FA, accountId)
    }

}
