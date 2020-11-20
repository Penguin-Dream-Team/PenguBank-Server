package club.pengubank.models

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
    val accountId = integer("accountId")
            .references(Accounts.id)
}

// Database Object DAO
class UserEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var email by Users.email
    var password by Users.password
    var registeredAt by Users.registeredAt
    var accountId by Users.accountId

    override fun toString(): String = "User($id, $email, $password, $registeredAt, $accountId)"

    fun toUser() = User(id.value, email, password, registeredAt.toString(), accountId)
}

// JSON Object DTO
data class User(
    val id: Int,
    val email: String,
    val password: String,
    val registeredAt: String,
    val accountId: Int
)