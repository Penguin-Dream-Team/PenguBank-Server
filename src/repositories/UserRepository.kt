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
        getUserOrNull(userId) ?: throw UserNotFoundException(userId)
    }

    fun getUser(email: String) = transaction {
        UserEntity.find { Users.email eq email }.firstOrNull() ?: throw UserWrongEmailException(email)
    }

    fun getUserOrNull(userId: Int) = transaction {
        UserEntity.findById(userId)
    }

    fun addUser(user: User) = transaction {
        UserEntity.new {
            this.email = user.email
            this.password = user.password
        }
    }

    fun deleteUser(userId: Int) = transaction {
        val user = getUser(userId)
        user.delete()
    }
}