package club.pengubank.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime

// Table Object
object Accounts: IntIdTable() {
    val balance = integer("balance")
}

// Database Object DAO
class AccountEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<AccountEntity>(Accounts)

    var balance by Accounts.balance

    override fun toString(): String = "Account($id, $balance)"

    fun toAccount() = Account(id.value, balance)
}

// JSON Object DTO
data class Account(
        val id: Int,
        val balance: Int
)