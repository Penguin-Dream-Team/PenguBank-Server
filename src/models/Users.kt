package club.pengubank.models

import club.pengubank.application.JWTAuthenticationConfig
import io.ktor.auth.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime

// Table Object
object Users: IntIdTable() {
    val email = varchar("email", 128).uniqueIndex()
    val password = varchar("password", 60)
    val registeredAt = datetime("registered_at").defaultExpression(CurrentDateTime())
}

// Database Object DAO
class UserEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var email by Users.email
    var password by Users.password
    var registeredAt by Users.registeredAt

    override fun toString(): String = "User($id, $email, $password, $registeredAt)"

    fun toUser() = User(id.value, email, password, registeredAt.toString())
    fun toUserWithToken() = UserWithJWT(toUser())
}

// JSON Object DTO
data class User(
    val id: Int,
    val email: String,
    val password: String,
    val registeredAt: String,
)

// JSON Object DTO
data class UserWithJWT(
    val user: User,
    val token: String = JWTAuthenticationConfig.makeToken(user)
) : Principal
