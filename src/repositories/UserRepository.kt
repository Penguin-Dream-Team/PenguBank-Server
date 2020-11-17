package club.pengubank.repositories

import club.pengubank.errors.exceptions.user.UserNotFoundException
import club.pengubank.errors.exceptions.user.UserWrongEmailException
import club.pengubank.models.UserReponse
import club.pengubank.models.UserEntity
import club.pengubank.models.Users
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {

    fun getAllUsers(): Iterable<UserEntity> = transaction {
        UserEntity.all()
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

    fun addUser(userReponse: UserReponse) = transaction {
        UserEntity.new {
            this.email = userReponse.email
            this.password = userReponse.password
        }
    }

    fun deleteUser(userId: Int) = transaction {
        val user = getUser(userId)
        user.delete()
    }
}