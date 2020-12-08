package controllers

import application.*
import io.ktor.application.*
import io.ktor.auth.authenticate
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.di.instance
import org.kodein.di.ktor.di
import responses.SuccessResponse
import services.TransactionService
import services.UserService

@KtorExperimentalLocationsAPI
fun Route.accounts() {
    val transactionService by di().instance<TransactionService>()
    val userService by di().instance<UserService>()

    authenticate("password-2fauth") {
        put<Transaction> {
            val userWithToken = call.user!!

            withContext(Dispatchers.IO) {
                val transactionValues = call.receive<TransactionRequest>()
                val user = userService.getUserById(userWithToken.user.id)

                val queuedTransactionResponse = transactionService.newTransaction(user.account!!.id, transactionValues.destinationId, transactionValues.amount)
                call.respond(SuccessResponse(data = queuedTransactionResponse, token = userWithToken.token))
            }
        }

        delete<CancelTransaction> {
            val userWithToken = call.user!!

            withContext(Dispatchers.IO) {
                val cancelRequest = call.receive<CancelTransactionRequest>()
                transactionService.cancelTransaction(userWithToken.user.id, cancelRequest.transactionId, cancelRequest.signedToken)
                call.respond(SuccessResponse(data = mapOf("ok" to true), token = userWithToken.token))
            }
        }

        patch<ApproveTransaction> {
            val userWithToken = call.user!!

            withContext(Dispatchers.IO) {
                val approveRequest = call.receive<ApproveTransactionRequest>()
                val transactionResponse = transactionService.approveTransaction(userWithToken.user.id, approveRequest.transactionId, approveRequest.signedToken)
                call.respond(SuccessResponse(data = transactionResponse, token = userWithToken.token))
            }
        }

        get<PendingTransactions> {
            val userWithToken = call.user!!

            withContext(Dispatchers.IO) {
                val transactionResponse = transactionService.getAllPendingTransactions(userWithToken.user.accountId)
                call.respond(SuccessResponse(data = transactionResponse, token = userWithToken.token))
            }
        }
    }
}

data class TransactionRequest(val destinationId: Int, val amount: Int)
data class ApproveTransactionRequest(val transactionId: Int, val signedToken: String)
data class CancelTransactionRequest(val transactionId: Int, val signedToken: String)

