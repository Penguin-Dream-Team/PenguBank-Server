package club.pengubank.repositories

import club.pengubank.models.User
import club.pengubank.models.UserEntity
import club.pengubank.models.Users
import io.ktor.features.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {

    fun getAllUsers(): Iterable<User> = transaction {
        UserEntity.all().map(UserEntity::toUser)
    }

    fun getUser(userId: Int) = transaction {
        UserEntity[userId]
    }

    fun getUser(email: String) = transaction {
        UserEntity.find { Users.email eq email }.firstOrNull() ?: throw NotFoundException("Entity UserEntity, email=$email not found in the database")
    }

    fun addUser(user: User) = transaction {
        UserEntity.new {
            this.email = user.email
            this.password = user.password
        }
    }

    fun deleteUser(userId: Int) = transaction {
        UserEntity[userId].delete()
    }
}