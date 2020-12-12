package services

import models.AccountEntity
import models.QueuedTransactionResponse
import models.TransactionConstants
import models.TransactionResponse
import org.apache.commons.codec.binary.Base64
import repositories.QueuedTransactionRepository
import repositories.UserRepository
import responses.exceptions.transaction.CannotSendTransactionToSelfException
import responses.exceptions.transaction.InvalidTransactionAmountException
import responses.exceptions.transaction.TransactionTokenInvalidException
import responses.exceptions.user.UserPhoneNotYetRegisteredException
import security.SecurityUtils
import java.time.Instant
import java.util.*

class TransactionService(
    private val userRepository: UserRepository,
    private val queuedTransactionRepository: QueuedTransactionRepository
) {

    fun getAllPendingTransactions(accountId: Int): Iterable<QueuedTransactionResponse> =
        queuedTransactionRepository.getAllQueuedTransactions(accountId)

    fun newTransaction(account: Int, destinationEmail: String, amount: Int): QueuedTransactionResponse {
        val destination: AccountEntity = userRepository.getUserAccount(destinationEmail)

        if (destination.id.value == account)
            throw CannotSendTransactionToSelfException()

        if (amount <= 0)
            throw InvalidTransactionAmountException()

        val token = Base64.encodeBase64String("${UUID.randomUUID()}${Instant.now().toEpochMilli()}".encodeToByteArray())

        return queuedTransactionRepository.addTransaction(
            account,
            destination,
            amount,
            token
        )
    }

    fun cancelTransaction(userId: Int, transactionId: Int, signedToken: String) {
        validateTransactionToken(userId, transactionId, signedToken)
        queuedTransactionRepository.cancelTransaction(transactionId)
    }

    fun approveTransaction(userId: Int, transactionId: Int, signedToken: String): TransactionResponse {
        validateTransactionToken(userId, transactionId, signedToken)
        return queuedTransactionRepository.approveTransaction(transactionId)
    }

    private fun validateTransactionToken(userId: Int, transactionId: Int, signedToken: String) {
        val token = queuedTransactionRepository.getToken(transactionId)

        val publicKey = userRepository.getUser(userId).phonePublicKey ?: throw UserPhoneNotYetRegisteredException()
        try {
            if (!SecurityUtils.verifySignature(token, signedToken, publicKey)) throw RuntimeException()
        } catch (e: Exception) {
            throw TransactionTokenInvalidException()
        }
    }

    fun performExpiredTransactionsCronJob() =
        queuedTransactionRepository.cancelExpiredTransactions(TransactionConstants.EXPIRED_INTERVAL)
}