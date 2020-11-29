package repositories

import models.Accounts
import models.QueuedTransactionEntity
import models.TransactionEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import responses.exceptions.transaction.TransactionNotFoundException

class QueuedTransactionRepository {

    fun addTransaction(accountId: Int, destinationId: Int, amount: Int) = transaction {
        QueuedTransactionEntity.new {
            this.amount = amount
            this.accountId = EntityID(accountId, Accounts)
            this.destinationId = EntityID(destinationId, Accounts)
        }.toQueuedTransactionResponse()
    }

    fun getTransaction(transactionId: Int) = transaction {
        QueuedTransactionEntity.findById(transactionId) ?: throw TransactionNotFoundException(transactionId)
    }

    fun deleteTransaction(transactionId: Int) = transaction {
        getTransaction(transactionId).delete()
    }
}