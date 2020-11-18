package club.pengubank.controllers

import club.pengubank.application.*
import club.pengubank.errors.exceptions.user.UserMissing2FACodeException
import club.pengubank.services.AuthService
import io.ktor.application.*
import io.ktor.auth.*
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
            val loggedUser = authService.login(loginValues.email, loginValues.password)
            val loginResult =
                if (loggedUser.enabled2FA)
                    loggedUser.toUserResponseWithToken()
                else
                    loggedUser.toUserResponseWith2FAToken()

            call.respond(loginResult)
        }
    }

    post<RegisterUser> {
        withContext(Dispatchers.IO) {
            val registerValues = call.receive<RegisterRequest>()
            val newUser = authService.register(registerValues)
            call.respond(mapOf("success" to "true"))
        }
    }

    authenticate("password-auth") {
        post<Verify2FA> {
            withContext(Dispatchers.IO) {
                val user = call.tempUser!!
                val verifyValues = call.receiveOrNull<Verify2FARequest>() ?: throw UserMissing2FACodeException()
                authService.verify2FA(verifyValues)
                call.respond(user.toUserResponseWith2FAToken())
            }
        }
    }
}

data class RegisterRequest(val email: String, val password: String, val confirmPassword: String)

data class LoginRequest(val email: String, val password: String)

data class Verify2FARequest(val code: String?)
