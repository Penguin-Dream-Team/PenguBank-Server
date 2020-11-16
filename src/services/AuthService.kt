package club.pengubank.services

import club.pengubank.errors.UnauthorizedException
import club.pengubank.models.UserEntity
import club.pengubank.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCrypt

class AuthService(private val userRepository: UserRepository) {

    fun login(email: String, password: String): UserEntity {
        val user = userRepository.getUser(email)

        if (BCrypt.checkpw(password, user.password))
        //if (BCrypt.checkpw(password, BCrypt.hashpw(user.password, BCrypt.gensalt(10))))
            return user
        else
        throw UnauthorizedException("Login Failed, Incorrect email or password")

        // TODO: password use bcrypt
    }
}
