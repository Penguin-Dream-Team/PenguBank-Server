package services

import models.*
import repositories.TransactionRepository
import repositories.QueuedTransactionRepository
import responses.exceptions.account.AccountNotEnoughBalanceException

class TransactionService(
    private  val transactionRepository: TransactionRepository,
    private  val queuedTransactionRepository: QueuedTransactionRepository
)
{
    fun getAllTransactions(accountId: Int): Iterable<TransactionResponse> = transactionRepository.getAllTransactions(accountId)

    fun getTransactionById(transactionId: Int): TransactionEntity = transactionRepository.getTransaction(transactionId)

    fun getAccountTransactions(accountId: Int): Iterable<TransactionEntity> = transactionRepository.getAccountTransactions(accountId)

    fun newTransaction(account: AccountResponse, destination: Int, amount: Int): QueuedTransactionIdWithToken {
        if (account.balance < amount) {
            throw AccountNotEnoughBalanceException(account.id)
        }

        // Generate Token
        val token = 3

        return QueuedTransactionIdWithToken(queuedTransactionRepository.addTransaction(account.id, destination, amount).id, token)
    }

    fun endQueuedTransaction(transactionId: Int): TransactionResponse {
        val queuedTransaction = queuedTransactionRepository.getTransaction(transactionId)
        queuedTransactionRepository.deleteTransaction(transactionId)

        return transactionRepository.addTransaction(queuedTransaction.accountId.value, queuedTransaction.destinationId.value, queuedTransaction.amount)
    }
}