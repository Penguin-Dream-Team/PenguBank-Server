package club.pengubank.controllers

import club.pengubank.application.LoginUser
import club.pengubank.services.AuthService
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.login() {
    val authService by di().instance<AuthService>()

    get<LoginUser> { loginValues ->
        val loginResult = authService.login(loginValues.email, loginValues.password)
        call.respond(loginResult.toUser())
    }
}