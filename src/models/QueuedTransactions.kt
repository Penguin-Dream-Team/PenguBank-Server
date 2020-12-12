package models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.select
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
    var token by QueuedTransactions.token
    var createdAt by QueuedTransactions.createdAt

    var account by AccountEntity referencedOn QueuedTransactions.accountId
    var destination by AccountEntity referencedOn QueuedTransactions.destinationId

    override fun toString(): String {
        return "QueuedTransactions($id, $amount, ${account.getUserEmail()}, ${destination.getUserEmail()}, $createdAt, $token)"
    }

    private fun expiredAt(): DateTime = createdAt.plus(TransactionConstants.EXPIRED_INTERVAL.toMillis())

    fun toQueuedTransactionResponse() =
        QueuedTransactionResponse(
            id.value,
            amount,
            account.getUserEmail(),
            destination.getUserEmail(),
            createdAt.toString(),
            expiredAt().toString(),
            token
        )
}

// JSON Object DTO
data class QueuedTransactionResponse(
    val id: Int,
    val amount: Int,
    val account: String,
    val destination: String,
    val createdAt: String,
    val expiredAt: String,
    val token: String
)
