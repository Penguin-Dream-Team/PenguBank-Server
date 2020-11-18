package club.pengubank.controllers

import club.pengubank.application.LoginUser
import club.pengubank.services.AuthService
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.login() {
    val authService by di().instance<AuthService>()

    post<LoginUser> {
        withContext(Dispatchers.IO) {
            val loginValues = call.receive<LoginRequest>()
            val loginResult = authService.login(loginValues.email, loginValues.password)
            call.respond(loginResult)
        }
    }
}

data class LoginRequest(val email: String, val password: String)