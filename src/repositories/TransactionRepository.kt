package repositories

import responses.exceptions.transaction.AccountTransactionsNotFoundException
import responses.exceptions.transaction.TransactionNotFoundException
import models.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class TransactionRepository {

    fun getAllTransactions(accountId: Int): Iterable<TransactionResponse> = transaction {
        TransactionEntity.all().map { it.toTransactionResponse(accountId) }
    }

    fun getTransaction(transactionId: Int) = transaction {
        TransactionEntity.findById(transactionId) ?: throw TransactionNotFoundException(transactionId)
    }

    // Isto precisa de execao??
    fun getAccountTransactions(accountId: Int) = transaction {
        TransactionEntity.find {Transactions.accountId eq accountId}.asIterable() ?: throw  AccountTransactionsNotFoundException(accountId)
    }

    fun addTransaction(accountId: Int, destinationId: Int, amount: Int) = transaction {
        AccountEntity[accountId].balance -= amount

        TransactionEntity.new {
            this.amount = amount
            this.accountId = EntityID(accountId, Accounts)
            this.destinationId = EntityID(destinationId, Accounts)
        }.toTransactionResponse(accountId)
    }

    fun deleteTransaction(transactionId: Int) = transaction {
        getTransaction(transactionId).delete()
    }
}