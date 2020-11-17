package club.pengubank.services

import club.pengubank.models.UserReponse
import club.pengubank.models.UserEntity
import club.pengubank.repositories.UserRepository

class UserService(private val userRepository: UserRepository) {

    fun getAllUsers(): Iterable<UserEntity> = userRepository.getAllUsers()

    fun getUserById(userId: Int): UserEntity = userRepository.getUser(userId)
}