package repositories

import models.*
import responses.exceptions.user.UserNotFoundException
import responses.exceptions.user.UserWrongEmailException
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {

    fun getAllUsers(): Iterable<User> = transaction {
        UserEntity.all().map(UserEntity::toUser)
    }

    fun getUser(userId: Int) = transaction {
        getUserOrNull(userId)?.toUser() ?: throw UserNotFoundException(userId)
    }

    fun getUser(email: String) = transaction {
        getUserOrNull(email)?.toUser() ?: throw UserWrongEmailException(email)
    }

    private fun getUserOrNull(userId: Int) = transaction {
        UserEntity.findById(userId)
    }

    private fun getUserOrNull(email: String) = transaction {
        UserEntity.find { Users.email eq email }.firstOrNull()
    }

    fun addUser(user: User) = transaction {
        UserEntity.create(user.email, user.password, user.secretKey).toUser()
    }

    fun hasUser(email: String): Boolean {
        return getUserOrNull(email) != null
    }

    fun activate2FA(userId: Int) = transaction {
        getUserOrNull(userId)!!.enabled2FA = true
    }

    fun addPhonePublicKey(userId: Int, phonePublicKey: String) = transaction {
        getUserOrNull(userId)!!.phonePublicKey = phonePublicKey
    }

    fun addPhoneMACAddress(userId: Int, phoneMACAddress: String) = transaction {
        getUserOrNull(userId)!!.phoneMACAddress = phoneMACAddress
    }

    fun hasPhonePublicKey(userId: Int): Boolean {
        return getUserOrNull(userId)?.phonePublicKey?.isNotBlank() ?: false
    }

    fun hasPhoneMACAddress(userId: Int): Boolean {
        return getUserOrNull(userId)?.phoneMACAddress?.isNotBlank() ?: false
    }
}