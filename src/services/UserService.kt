package services

import models.User
import models.UserEntity
import repositories.UserRepository

class UserService(private val userRepository: UserRepository) {

    fun getAllUsers(): Iterable<User> = userRepository.getAllUsers()

    fun getUserById(userId: Int): User = userRepository.getUser(userId)
}