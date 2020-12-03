package models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

// Table Object
object QueuedTransactions : IntIdTable() {
    val amount = integer("amount")
    val accountId = reference("account_id", Accounts, onDelete = ReferenceOption.CASCADE)
    val destinationId = reference("destination_id", Accounts, onDelete = ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime())
    val token = varchar("token", 128)
}

// Database Object DAO
class QueuedTransactionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<QueuedTransactionEntity>(QueuedTransactions)

    var amount by QueuedTransactions.amount
    var accountId by QueuedTransactions.accountId
    var destinationId by QueuedTransactions.destinationId
    var token by QueuedTransactions.token
    var createdAt by QueuedTransactions.createdAt

    fun toString(accountId: Int): String {
        return "QueuedTransactions($id, $amount, $accountId, $destinationId, $createdAt, $token)"
    }

    private fun expiredAt(): DateTime = createdAt.plus(TransactionConstants.EXPIRED_INTERVAL.toMillis())

    fun toQueuedTransactionResponse() =
        QueuedTransactionResponse(
            id.value,
            amount,
            accountId.value,
            destinationId.value,
            createdAt.toString(),
            expiredAt().toString()
        )
}

// JSON Object DTO
data class QueuedTransactionResponse(
    val id: Int,
    val amount: Int,
    val account: Int,
    val destination: Int,
    val createdAt: String,
    val expiredAt: String
) {
    fun toQueuedTransactionIdResponseWithToken(token: String): QueuedTransactionIdWithToken =
        QueuedTransactionIdWithToken(id, token)
}

// JSON Object DTO
data class QueuedTransactionIdWithToken(
    val id: Int,
    val token: String
)