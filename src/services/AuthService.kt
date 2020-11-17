package club.pengubank.services

import club.pengubank.errors.exceptions.user.UserWrongCredentialsException
import club.pengubank.models.UserEntity
import club.pengubank.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCrypt

class AuthService(private val userRepository: UserRepository) {

    fun login(email: String, password: String): UserEntity {
        val user = userRepository.getUser(email)

        return if (BCrypt.checkpw(password, user.password)) user else throw UserWrongCredentialsException()
    }
}
