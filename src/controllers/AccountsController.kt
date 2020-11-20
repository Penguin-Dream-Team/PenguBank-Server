package club.pengubank.controllers

import club.pengubank.application.About
import club.pengubank.application.Accounts
import club.pengubank.application.Home
import club.pengubank.application.Transactions
import club.pengubank.services.AccountService
import club.pengubank.services.UserService
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