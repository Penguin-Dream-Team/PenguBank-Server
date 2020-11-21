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
    val enabled2FA = bool("enabled_2fa").default(false)
}

// Database Object DAO
class UserEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var email by Users.email
    var password by Users.password
    var registeredAt by Users.registeredAt
    var enabled2FA by Users.enabled2FA

    override fun toString(): String = "User($id, $email, $password, $registeredAt, $enabled2FA)"

    fun toUser() = User(id.value, email, password, registeredAt.toString(), enabled2FA)
}

data class User(
    val id: Int? = null,
    val email: String,
    val password: String,
    val registeredAt: String? = null,
    val enabled2FA: Boolean = false
) {
    fun toUserResponse() = UserResponse(id!!, email, registeredAt!!, enabled2FA)
    fun toUserResponseWithToken() = UserResponseWithJWT(toUserResponse())
    fun toUserResponseWith2FAToken() = UserResponseWith2FAJWT(toUserResponse())
}

// JSON Object DTO
data class UserResponse(
    val id: Int,
    val email: String,
    val registeredAt: String,
    val enabled2FA: Boolean
)

// JSON Object DTO
data class UserResponseWithJWT(
    val user: UserResponse,
    val token: String = JWTAuthenticationConfig.makeToken(user)
) : Principal {
    fun toUserResponseWith2FAToken() = UserResponseWith2FAJWT(user)
}

// JSON Object DTO
data class UserResponseWith2FAJWT(
    val user: UserResponse,
    val verification2FA: Boolean = true,
    val token: String = JWTAuthenticationConfig.make2FAToken(user)
) : Principal
