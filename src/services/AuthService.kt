package services

import application.JWTClaims
import controllers.RegisterRequest
import controllers.Verify2FARequest
import responses.exceptions.user.UserInvalid2FACodeException
import responses.exceptions.user.UserRegistrationDuplicateEmailException
import responses.exceptions.user.UserRegistrationPasswordsDoNotMatch
import responses.exceptions.user.UserWrongCredentialsException
import models.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.springframework.security.crypto.bcrypt.BCrypt
import repositories.UserRepository

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
        return userRepository.addUser(User(email = registerValues.email, password = cipheredPassword))
    }

    fun verify2FA(verifyValues: Verify2FARequest) {
        verifyValues.code ?: throw UserInvalid2FACodeException()
        if(verifyValues.code != "1337")
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
