package club.pengubank.services

import club.pengubank.controllers.RegisterRequest
import club.pengubank.controllers.Verify2FARequest
import club.pengubank.responses.exceptions.user.UserInvalid2FACodeException
import club.pengubank.responses.exceptions.user.UserRegistrationDuplicateEmailException
import club.pengubank.responses.exceptions.user.UserRegistrationPasswordsDoNotMatch
import club.pengubank.responses.exceptions.user.UserWrongCredentialsException
import club.pengubank.models.*
import club.pengubank.repositories.UserRepository
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.springframework.security.crypto.bcrypt.BCrypt

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
        //TODO("GENERATE CODE AND VALIDATE")
    }

    fun validateUserJWT(credential: JWTCredential): Principal? {
        val id = credential.payload.getClaim("id").asInt()
        val email = credential.payload.getClaim("email").asString()
        val registeredAt = credential.payload.getClaim("registeredAt").asString()

        return UserResponseWithJWT(UserResponse(id, email, registeredAt))
    }

    fun validateUser2FAJWT(credential: JWTCredential): Principal? {
        val id = credential.payload.getClaim("id").asInt()
        val email = credential.payload.getClaim("email").asString()
        val registeredAt = credential.payload.getClaim("registeredAt").asString()
        val verification2FA = credential.payload.getClaim("verification2FA").asBoolean()

        return UserResponseWith2FAJWT(UserResponse(id, email, registeredAt), verification2FA)
    }

}
