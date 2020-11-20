package club.pengubank.repositories

import club.pengubank.errors.exceptions.transaction.AccountTransactionsNotFoundException
import club.pengubank.errors.exceptions.transaction.TransactionNotFoundException
import club.pengubank.models.*
import org.jetbrains.exposed.sql.transactions.transaction

class TransactionRepository {

    fun getAllTransactions(): Iterable<Transaction> = transaction {
        TransactionEntity.all().map(TransactionEntity::toTransation)
    }

    fun getTransaction(transactionId: Int) = transaction {
        TransactionEntity.findById(transactionId) ?: throw TransactionNotFoundException(transactionId)
    }

    // Isto precisa de execao??
    fun getAccountTransactions(accountId: Int) = transaction {
        TransactionEntity.find {Transactions.accountId eq accountId}.asIterable() ?: throw  AccountTransactionsNotFoundException(accountId)
    }

    fun addTransaction(newtransaction: Transaction) = transaction {
        TransactionEntity.new {
            this.amount = newtransaction.amount
            this.accountId = newtransaction.accountId
        }
    }

    fun deleteTransaction(transactionId: Int) = transaction {
        val oldTransaction = getTransaction(transactionId)
        oldTransaction.delete()
    }
}