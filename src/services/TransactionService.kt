package club.pengubank.services

import club.pengubank.models.Transaction
import club.pengubank.models.TransactionEntity
import club.pengubank.repositories.TransactionRepository

class TransactionService(private  val transactionRepository: TransactionRepository) {

    fun getAllTransactions(): Iterable<Transaction> = transactionRepository.getAllTransactions()

    fun getTransactionById(transactionId: Int): TransactionEntity = transactionRepository.getTransaction(transactionId)

    fun getAccountTransactions(accountId: Int): Iterable<TransactionEntity> = transactionRepository.getAccountTransactions(accountId)
}