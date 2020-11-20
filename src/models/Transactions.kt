package club.pengubank.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime

// Table Object
object Transactions: IntIdTable() {
    val amount = integer("amount")
    val accountId = integer("accountId")
            .references(Accounts.id)
}

// Database Object DAO
class TransactionEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<TransactionEntity>(Transactions)

    var amount by Transactions.amount
    var accountId by Transactions.accountId

    override fun toString(): String = "Transaction($id, $amount, $accountId)"

    fun toTransation() = Transaction(id.value, amount, accountId)
}

// JSON Object DTO
data class Transaction(
        val id: Int,
        val amount: Int,
        var accountId: Int
)