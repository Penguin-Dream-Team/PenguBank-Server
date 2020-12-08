package services

import models.QueuedTransactionResponse
import models.TransactionConstants
import models.TransactionResponse
import org.apache.commons.codec.binary.Base64
import repositories.QueuedTransactionRepository
import repositories.TransactionRepository
import responses.exceptions.transaction.CannotSendTransactionToSelfException
import responses.exceptions.transaction.TransactionTokenInvalidException
import java.security.SecureRandom
import java.time.Instant
import java.util.*

class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val queuedTransactionRepository: QueuedTransactionRepository
) {

    fun getAllPendingTransactions(accountId: Int): Iterable<QueuedTransactionResponse> =
        queuedTransactionRepository.getAllQueuedTransactions(accountId)

    fun newTransaction(account: Int, destination: Int, amount: Int): QueuedTransactionResponse {
        if (account == destination) throw CannotSendTransactionToSelfException()

        // Generate Token
        val token = Base64.encodeBase64String("${UUID.randomUUID()}${Instant.now().toEpochMilli()}".encodeToByteArray())

        return queuedTransactionRepository.addTransaction(
            account,
            destination,
            amount,
            token
        )
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