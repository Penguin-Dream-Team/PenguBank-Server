package controllers

import application.Accounts
import application.Transaction
import application.guest
import application.user
import io.ktor.application.*
import io.ktor.auth.authenticate
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.di.instance
import org.kodein.di.ktor.di
import responses.SuccessResponse
import services.AccountService
import services.TransactionService
import services.UserService

@KtorExperimentalLocationsAPI
fun Route.accounts() {
    val accountService by di().instance<AccountService>()
    val transactionService by di().instance<TransactionService>()
    val userService by di().instance<UserService>()

    get<Accounts> {
        call.respond(accountService.getAllAccounts())
    }

    authenticate("password-2fauth") {
        put<Transaction> {
            withContext(Dispatchers.IO) {
                val transactionValues = call.receive<TransactionRequest>()
                val userWithToken = call.user!!
                val user = userService.getUserById(userWithToken.user.id)

                val queuedTransactionResponse = transactionService.newTransaction(user.account!!, transactionValues.accountDestinationId, transactionValues.amount)
                call.respond(SuccessResponse(data = queuedTransactionResponse, token = userWithToken.token))
            }
        }

        put<Transaction> {
            withContext(Dispatchers.IO) {
                val transactionValues = call.receive<EndTransactionRequest>()
                // if not signed return error

                val userWithToken = call.user!!

                val transactionResponse = transactionService.endQueuedTransaction(transactionValues.transactionId)
                call.respond(SuccessResponse(data = transactionResponse, token = userWithToken.token))
            }
        }
    }
}

data class TransactionRequest(val accountDestinationId: Int, val amount: Int)
data class EndTransactionRequest(val transactionId: Int, val signedToken: Int)

