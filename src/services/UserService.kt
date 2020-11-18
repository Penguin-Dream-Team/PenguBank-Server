package club.pengubank.services

import club.pengubank.models.User
import club.pengubank.models.UserEntity
import club.pengubank.repositories.UserRepository

class UserService(private val userRepository: UserRepository) {

    fun getAllUsers(): Iterable<User> = userRepository.getAllUsers()

    fun getUserById(userId: Int): User = userRepository.getUser(userId)
}