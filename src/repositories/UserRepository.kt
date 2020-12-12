package repositories

import models.AccountEntity
import models.User
import models.UserEntity
import models.Users
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

    fun hasUser(email: String): Boolean = getUserOrNull(email) != null

    fun activate2FA(userId: Int) = transaction {
        getUserOrNull(userId)!!.enabled2FA = true
    }

    fun addPhonePublicKey(userId: Int, phonePublicKey: String) = transaction {
        getUserOrNull(userId)!!.phonePublicKey = phonePublicKey
    }

    fun hasPhonePublicKey(userId: Int): Boolean = getUserOrNull(userId)?.phonePublicKey?.isNotBlank() ?: false

    fun getUserAccount(email: String): AccountEntity = transaction {
        getUserOrNull(email)?.account ?: throw UserWrongEmailException(email)
    }
}