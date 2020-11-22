package models

import application.DEFAULT_ACCOUNT_BALANCE
import application.JWTAuthenticationConfig
import io.ktor.auth.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime

// Table Object
object Users: IntIdTable() {
    val email = varchar("email", 128).uniqueIndex()
    val password = varchar("password", 60)
    val registeredAt = datetime("registered_at").defaultExpression(CurrentDateTime())
    val enabled2FA = bool("enabled_2fa").default(false)
    val accountId = reference("account_id", Accounts, ReferenceOption.CASCADE)
}

// Database Object DAO
class UserEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users) {
        fun create(email: String, password: String): UserEntity {
            return new {
                this.email = email
                this.password = password
                this.account = AccountEntity.new {
                    this.balance = DEFAULT_ACCOUNT_BALANCE
                }
            }
        }
    }

    var email by Users.email
    var password by Users.password
    var registeredAt by Users.registeredAt
    var account by AccountEntity referencedOn Users.accountId
    var enabled2FA by Users.enabled2FA

    override fun toString(): String = "User($id, $email, $password, $registeredAt, $enabled2FA, ${account.toAccountResponse()})"

    fun toUser() = User(id.value, email, password, registeredAt.toString(), enabled2FA, account.toAccountResponse())
}

data class User(
    val id: Int? = null,
    val email: String,
    val password: String,
    val registeredAt: String? = null,
    val enabled2FA: Boolean = false,
    val account: AccountResponse? = null
) {
    fun toSimpleUserResponse() = SimpleUserResponse(id!!, email, registeredAt!!, enabled2FA, account!!.id)
    fun toUserResponseWithToken() = UserResponseWithJWT(toSimpleUserResponse())
    fun toUserResponseWith2FAToken() = UserResponseWith2FAJWT(toSimpleUserResponse())
}

// JSON Object DTO
data class SimpleUserResponse(
    val id: Int,
    val email: String,
    val registeredAt: String,
    val enabled2FA: Boolean,
    val accountId: Int
)

// JSON Object DTO
data class UserResponseWithJWT(
    val user: SimpleUserResponse,
    val token: String = JWTAuthenticationConfig.makeToken(user)
) : Principal {
    fun toUserResponseWith2FAToken() = UserResponseWith2FAJWT(user)
}

// JSON Object DTO
data class UserResponseWith2FAJWT(
    val user: SimpleUserResponse,
    val verification2FA: Boolean = true,
    val token: String = JWTAuthenticationConfig.make2FAToken(user)
) : Principal
