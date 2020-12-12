package repositories

import models.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import responses.exceptions.transaction.TransactionNotFoundException
import models.Accounts
import models.QueuedTransactionEntity
import models.QueuedTransactions
import models.TransactionResponse
import java.time.Duration

class QueuedTransactionRepository(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) {

    fun addTransaction(accountId: Int, destination: AccountEntity, amount: Int, token: String) = transaction {
        accountRepository.removeBalance(accountId, amount)

        QueuedTransactionEntity.new {
            this.amount = amount
            this.account = AccountEntity[accountId]
            this.destination = destination
            this.token = token
        }.toQueuedTransactionResponse()
    }

    private fun getQueuedTransaction(queuedTransactionId: Int) = transaction {
        QueuedTransactionEntity.findById(queuedTransactionId) ?: throw TransactionNotFoundException(queuedTransactionId)
    }

    fun approveTransaction(queuedTransactionId: Int): TransactionResponse = transaction {
        val queuedTransaction = getQueuedTransaction(queuedTransactionId)
        transactionRepository.approveTransaction(queuedTransaction)
    }

    fun cancelTransaction(queuedTransactionId: Int) = cancelTransaction(getQueuedTransaction(queuedTransactionId))

    private fun cancelTransaction(queuedTransaction: QueuedTransactionEntity) = transaction {
        accountRepository.addBalance(queuedTransaction.account.id.value, queuedTransaction.amount)

        queuedTransaction.delete()
    }

    fun cancelExpiredTransactions(expiredInterval: Duration) = transaction {
        QueuedTransactionEntity.find {
            QueuedTransactions.createdAt.less(DateTime.now().minus(expiredInterval.toMillis()))
        }.forEach(this@QueuedTransactionRepository::cancelTransaction)
    }

    fun getToken(transactionId: Int): String = transaction {
        getQueuedTransaction(transactionId).token
    }

    fun getAllQueuedTransactions(accountId: Int): Iterable<QueuedTransactionResponse> = transaction {
        QueuedTransactionEntity.find { QueuedTransactions.accountId.eq(accountId) }
            .map(QueuedTransactionEntity::toQueuedTransactionResponse)
    }

}