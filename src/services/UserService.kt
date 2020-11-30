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

    fun setupPhone(loggedUser: SimpleUserResponse, phoneMACAddress: String, phonePublicKey: String) {
        storePhonePublicKey(loggedUser, phonePublicKey)
        storePhoneMACAddress(loggedUser, phoneMACAddress)
    }

    private fun storePhoneMACAddress(user: SimpleUserResponse, phoneMACAddress: String) {
        if (hasPhoneMACAddress(user.id))
            throw UserPhoneAlreadyRegisteredException()
        userRepository.addPhoneMACAddress(user.id, phoneMACAddress)
    }

    private fun storePhonePublicKey(user: SimpleUserResponse, phonePublicKey: String) {
        if (hasPhonePublicKey(user.id))
            throw UserPhoneAlreadyRegisteredException()
        userRepository.addPhonePublicKey(user.id, phonePublicKey)
    }

    private fun hasPhonePublicKey(userId: Int): Boolean = userRepository.hasPhonePublicKey(userId)

    private fun hasPhoneMACAddress(userId: Int): Boolean = userRepository.hasPhoneMACAddress(userId)
}