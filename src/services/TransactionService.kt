package services

import models.TransactionResponse
import models.TransactionEntity
import repositories.TransactionRepository

class TransactionService(private  val transactionRepository: TransactionRepository) {

    fun getAllTransactions(accountId: Int): Iterable<TransactionResponse> = transactionRepository.getAllTransactions(accountId)

    fun getTransactionById(transactionId: Int): TransactionEntity = transactionRepository.getTransaction(transactionId)

    fun getAccountTransactions(accountId: Int): Iterable<TransactionEntity> = transactionRepository.getAccountTransactions(accountId)
}