package repositories

import responses.exceptions.transaction.AccountTransactionsNotFoundException
import responses.exceptions.transaction.TransactionNotFoundException
import models.*
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

    fun addTransaction(newtransaction: TransactionResponse) = transaction {
        TransactionEntity.new {
            this.amount = newtransaction.amount
            //this.accountId = newtransaction.accountId
        }
    }

    fun deleteTransaction(transactionId: Int) = transaction {
        val oldTransaction = getTransaction(transactionId)
        oldTransaction.delete()
    }
}