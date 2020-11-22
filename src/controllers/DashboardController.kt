package controllers

import application.Dashboard
import application.user
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.di.instance
import org.kodein.di.ktor.di
import responses.SuccessResponse
import services.AccountService

@KtorExperimentalLocationsAPI
fun Route.dashboard() {
    val accountService by di().instance<AccountService>()

    authenticate("password-2fauth") {
        get<Dashboard> {
            withContext(Dispatchers.IO) {
                val userWithToken = call.user!!
                val account = accountService.getAccountById(userWithToken.user.accountId)
                call.respond(SuccessResponse(data = account.toAccountResponse(), token = userWithToken.token))
            }
        }
    }

}
