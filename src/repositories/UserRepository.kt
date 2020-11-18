package club.pengubank.repositories

import club.pengubank.errors.exceptions.user.UserNotFoundException
import club.pengubank.errors.exceptions.user.UserWrongEmailException
import club.pengubank.models.User
import club.pengubank.models.UserEntity
import club.pengubank.models.Users
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
        UserEntity.new {
            this.email = user.email
            this.password = user.password
        }.toUser()
    }

    fun hasUser(email: String): Boolean {
        return getUserOrNull(email) != null
    }
}