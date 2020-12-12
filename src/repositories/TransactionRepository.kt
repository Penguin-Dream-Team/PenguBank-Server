package repositories

import models.QueuedTransactionEntity
import models.TransactionEntity
import models.TransactionResponse
import models.Transactions
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction

class TransactionRepository(private val accountRepository: AccountRepository) {

    fun approveTransaction(queuedTransaction: QueuedTransactionEntity): TransactionResponse = transaction {
        val transaction = TransactionEntity.new {
            this.amount = queuedTransaction.amount
            this.accountId = queuedTransaction.account.id
            this.destinationId = queuedTransaction.destination.id
            this.createdAt = queuedTransaction.createdAt
        }

        queuedTransaction.delete()

        accountRepository.addBalance(transaction.destinationId.value, transaction.amount)

        transaction.toTransactionResponse(transaction.accountId.value)
    }
}