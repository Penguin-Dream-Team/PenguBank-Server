package club.pengubank.services

import club.pengubank.repositories.UserRepository

class AuthService(private val userRepository: UserRepository) {

    fun login(email: String, password: String) {
        val user = userRepository.getUser(email)

        // if user is null, Account with email not foudn exception

        // TODO: password use bcrypt
    }

}
