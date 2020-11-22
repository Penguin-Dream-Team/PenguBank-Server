package controllers

import application.Accounts
import application.Transactions
import services.AccountService
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.accounts() {
    val accountService by di().instance<AccountService>()

    get<Accounts> {
        call.respond(accountService.getAllAccounts())
    }

    get<Transactions> {
        call.respondText ("Hello transaction")
    }
}
