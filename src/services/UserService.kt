package services

import club.pengubank.responses.exceptions.user.UserPhoneAlreadyRegisteredException
import models.SimpleUserResponse
import models.User
import repositories.UserRepository

class UserService(private val userRepository: UserRepository) {

    fun getAllUsers(): Iterable<User> = userRepository.getAllUsers()

    fun getUserById(userId: Int): User = userRepository.getUser(userId)

    fun activate2FA(userId: Int): User {
        userRepository.activate2FA(userId)
        return userRepository.getUser(userId)
    }

    fun setupPhone(loggedUser: SimpleUserResponse, phonePublicKey: String) {
        storePhonePublicKey(loggedUser, phonePublicKey)
    }

    private fun storePhonePublicKey(user: SimpleUserResponse, phonePublicKey: String) {
        if (hasPhonePublicKey(user.id))
            throw UserPhoneAlreadyRegisteredException()
        userRepository.addPhonePublicKey(user.id, phonePublicKey)
    }

    private fun hasPhonePublicKey(userId: Int): Boolean = userRepository.hasPhonePublicKey(userId)
}