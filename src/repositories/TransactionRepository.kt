package repositories

import models.QueuedTransactionEntity
import models.TransactionEntity
import models.TransactionResponse
import models.Transactions
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction

class TransactionRepository(private val accountRepository: AccountRepository) {

    fun getAllTransactions(accountId: Int): Iterable<TransactionResponse> = transaction {
        TransactionEntity.find { Transactions.accountId.eq(accountId) or Transactions.destinationId.eq(accountId) }
            .map(TransactionEntity::toTransactionResponse)
    }

    fun approveTransaction(queuedTransaction: QueuedTransactionEntity): TransactionResponse = transaction {

        val transaction = TransactionEntity.new {
            this.amount = queuedTransaction.amount
            this.accountId = queuedTransaction.accountId
            this.destinationId = queuedTransaction.destinationId
            this.createdAt = queuedTransaction.createdAt
        }

        queuedTransaction.delete()

        accountRepository.addBalance(transaction.destinationId.value, transaction.amount)

        transaction.toTransactionResponse()
    }
}