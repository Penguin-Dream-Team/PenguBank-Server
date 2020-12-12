package models

import application.DEFAULT_ACCOUNT_BALANCE
import application.JWTAuthenticationConfig
import io.ktor.auth.*
import io.ktor.util.*
import org.apache.commons.codec.binary.Base64
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import responses.exceptions.user.UserPhoneNotYetRegisteredException
import security.SecurityUtils
import totp.TOTPSecretKey

// Table Object
object Users : IntIdTable() {
    val email = varchar("email", 128).uniqueIndex()
    val password = varchar("password", 60)
    val registeredAt = datetime("registered_at").defaultExpression(CurrentDateTime())
    val enabled2FA = bool("enabled_2fa").default(false)
    val accountId = reference("account_id", Accounts, ReferenceOption.CASCADE)
    val secretKey = varchar("secret_key", 128)
    val iv = varchar("iv", 128)
    val tagLen = integer("tag_len")
    val phonePublicKey = text("phone_public_key").nullable()
}

// Database Object DAO
class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users) {
        fun create(email: String, password: String, secretKey: String): UserEntity {
            return new {
                this.email = email
                this.password = password
                this.account = AccountEntity.new {
                    this.balance = DEFAULT_ACCOUNT_BALANCE
                }
                val (ciphered, params) = SecurityUtils.cipher(secretKey)
                this.cipheredSecretKey = ciphered
                this.iv = Base64.encodeBase64String(params.iv)
                this.tagLen = params.tLen
            }
        }
    }

    var email by Users.email
    var password by Users.password
    var registeredAt by Users.registeredAt
    var account by AccountEntity referencedOn Users.accountId
    var enabled2FA by Users.enabled2FA
    var cipheredSecretKey by Users.secretKey
    var iv by Users.iv
    var tagLen by Users.tagLen
    var phonePublicKey by Users.phonePublicKey

    var secretKey = ""
        get() = if (!iv.isNullOrBlank()) SecurityUtils.decipher(cipheredSecretKey, Base64.decodeBase64(iv), tagLen) else ""

    override fun toString(): String =
        "User($id, $email, $password, $registeredAt, $enabled2FA, ${account.toAccountResponse()}, $phonePublicKey)"

    fun toUser() = User(
        id.value,
        email,
        password,
        registeredAt.toString(),
        enabled2FA,
        account.toAccountResponse(),
        secretKey,
        phonePublicKey
    )
}

data class User(
    val id: Int? = null,
    val email: String,
    val password: String,
    val registeredAt: String? = null,
    val enabled2FA: Boolean = false,
    val account: AccountResponse? = null,
    val secretKey: String,
    val phonePublicKey: String? = null
) {
    fun toSimpleUserResponse() =
        SimpleUserResponse(id!!, email, registeredAt!!, enabled2FA, account!!.id, phonePublicKey)

    fun toUserResponseWithToken() = UserResponseWithJWT(toSimpleUserResponse())
    fun toUserResponseWith2FAToken() = UserResponseWith2FAJWT(toSimpleUserResponse())

    fun getSecretKey(): TOTPSecretKey = TOTPSecretKey.from(value = secretKey)
    fun checkIfHasPhoneKey() {
        if (phonePublicKey.isNullOrBlank()) throw UserPhoneNotYetRegisteredException()
    }
}

// JSON Object DTO
data class SimpleUserResponse(
    val id: Int,
    val email: String,
    val registeredAt: String,
    val enabled2FA: Boolean,
    val accountId: Int,
    val phonePublicKey: String? = null
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
