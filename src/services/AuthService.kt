package club.pengubank.services

import club.pengubank.errors.exceptions.user.UserWrongCredentialsException
import club.pengubank.models.UserEntity
import club.pengubank.models.UserResponse
import club.pengubank.models.UserResponseWithJWT
import club.pengubank.repositories.UserRepository
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.springframework.security.crypto.bcrypt.BCrypt

class AuthService(private val userRepository: UserRepository) {

    fun login(email: String, password: String): UserEntity {
        val user = userRepository.getUser(email)

        return if (BCrypt.checkpw(password, user.password)) user else throw UserWrongCredentialsException()
    }

    fun validateUserJWT(credential: JWTCredential): Principal? {
        val id = credential.payload.getClaim("id").asInt()
        val email = credential.payload.getClaim("email").asString()
        val registeredAt = credential.payload.getClaim("registeredAt").asString()

        return UserResponse(id, email, registeredAt).toUserResponseWithToken()
    }

}
