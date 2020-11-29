package models

import application.DEFAULT_ACCOUNT_BALANCE
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime

// Table Object
object Transactions : IntIdTable() {
    val amount = integer("amount")
    val accountId = reference("account_id", Accounts, onDelete = ReferenceOption.CASCADE)
    val destinationId = reference("destination_id", Accounts, onDelete = ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime())
}

// Database Object DAO
class TransactionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TransactionEntity>(Transactions)

    var amount by Transactions.amount
    var accountId by Transactions.accountId
    var destinationId by Transactions.destinationId
    var createdAt by Transactions.createdAt

    private val account by AccountEntity referencedOn Transactions.accountId
    private val destination by AccountEntity referencedOn Transactions.destinationId

    fun toString(accountId: Int): String {
        return "Transaction($id, $amount, ${getType(accountId)}, ${getOther(accountId)}, $createdAt)"
    }

    fun toTransactionResponse(accountId: Int): TransactionResponse =
        TransactionResponse(id.value, amount, getType(accountId).toString(), getOther(accountId), createdAt.toString())

    private fun getOther(accountId: Int) =
        if (destination.id.value == accountId) account.getUserEmail()
        else destination.getUserEmail()

    private fun getType(accountId: Int) =
        if (destination.id.value == accountId) TransactionType.RECEIVED else TransactionType.SENT
}

// JSON Object DTO
data class TransactionResponse(
    val id: Int,
    val amount: Int,
    val type: String,
    val other: String,
    val createdAt: String
)

enum class TransactionType(val type: String) {
    RECEIVED("RECEIVED"),
    SENT("SENT");

    override fun toString() = type
}
