package services

import application.Transaction
import models.*
import repositories.TransactionRepository
import responses.exceptions.account.AccountNotEnoughBalanceException

class TransactionService(private  val transactionRepository: TransactionRepository) {

    fun getAllTransactions(accountId: Int): Iterable<TransactionResponse> = transactionRepository.getAllTransactions(accountId)

    fun getTransactionById(transactionId: Int): TransactionEntity = transactionRepository.getTransaction(transactionId)

    fun getAccountTransactions(accountId: Int): Iterable<TransactionEntity> = transactionRepository.getAccountTransactions(accountId)

    fun newTransaction(account: AccountResponse, destination: Int, amount: Int): TransactionResponse {
        if (account.balance < amount) {
            throw AccountNotEnoughBalanceException(account.id)
        }

        // Queue request
        // return
        // HIGH SECURITY PROTOCOL

        return transactionRepository.addTransaction(account.id, destination, amount)
    }
}