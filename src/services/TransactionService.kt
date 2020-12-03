package services

import responses.exceptions.transaction.CannotSendTransactionToSelfException
import responses.exceptions.transaction.TransactionTokenInvalidException
import models.QueuedTransactionIdWithToken
import models.TransactionConstants
import models.TransactionResponse
import repositories.QueuedTransactionRepository
import repositories.TransactionRepository

class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val queuedTransactionRepository: QueuedTransactionRepository
) {
    fun getAllTransactions(accountId: Int): Iterable<TransactionResponse> =
        transactionRepository.getAllTransactions(accountId)

    fun newTransaction(account: Int, destination: Int, amount: Int): QueuedTransactionIdWithToken {
        if (account == destination) throw CannotSendTransactionToSelfException()

        // Generate Token
        val token = "123"

        return queuedTransactionRepository.addTransaction(
            account,
            destination,
            amount,
            token
        ).toQueuedTransactionIdResponseWithToken(token)
    }

    fun cancelTransaction(transactionId: Int, signedToken: String) {
        validateTransactionToken(transactionId, signedToken)
        queuedTransactionRepository.cancelTransaction(transactionId)
    }

    fun approveTransaction(transactionId: Int, signedToken: String): TransactionResponse {
        validateTransactionToken(transactionId, signedToken)
        return queuedTransactionRepository.approveTransaction(transactionId)
    }

    private fun validateTransactionToken(transactionId: Int, signedToken: String) {
        val token = queuedTransactionRepository.getToken(transactionId)

        if (token != signedToken) throw TransactionTokenInvalidException()
    }

    fun performExpiredTransactionsCronJob() =
        queuedTransactionRepository.cancelExpiredTransactions(TransactionConstants.EXPIRED_INTERVAL)
}