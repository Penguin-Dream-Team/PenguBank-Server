package services

import models.User
import repositories.UserRepository
import totp.TOTPSecretKey

class UserService(private val userRepository: UserRepository) {

    fun getAllUsers(): Iterable<User> = userRepository.getAllUsers()

    fun getUserById(userId: Int): User = userRepository.getUser(userId)

    fun activate2FA(userId: Int): User {
        userRepository.activate2FA(userId)
        return userRepository.getUser(userId)
    }

    fun storePhonePublicKey(userId: Int, phonePublicKey: String) = userRepository.addPhonePublicKey(userId, phonePublicKey)

    fun hasPhonePublicKey(userId: Int): Boolean = userRepository.hasPhonePublicKey(userId)
}