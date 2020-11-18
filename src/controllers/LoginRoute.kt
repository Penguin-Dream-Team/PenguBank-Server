package club.pengubank.controllers

import club.pengubank.application.*
import club.pengubank.responses.exceptions.user.UserMissing2FACodeException
import club.pengubank.responses.SuccessResponse
import club.pengubank.responses.exceptions.user.UserAlreadyLoggedInException
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

    authenticate("password-auth", "password-2fauth", optional = true) {
        post<LoginUser> {
            if (call.user != null || call.tempUser != null) throw UserAlreadyLoggedInException()

            withContext(Dispatchers.IO) {
                val loginValues = call.receive<LoginRequest>()
                val loggedUser = authService.login(loginValues.email, loginValues.password)
                val token =
                    if (loggedUser.enabled2FA)
                        loggedUser.toUserResponseWithToken().token
                    else
                        loggedUser.toUserResponseWith2FAToken().token

                call.respond(SuccessResponse(data = loggedUser.toUserResponse(), token = token))
            }
        }

        post<RegisterUser> {
            if (call.user != null || call.tempUser != null) throw UserAlreadyLoggedInException()

            withContext(Dispatchers.IO) {
                val registerValues = call.receive<RegisterRequest>()
                val newUser = authService.register(registerValues)
                call.respond(SuccessResponse(data = newUser.toUserResponse()))
            }
        }
    }

    authenticate("password-auth") {
        post<Verify2FA> {
            withContext(Dispatchers.IO) {
                val user = call.tempUser!!
                val verifyValues = call.receiveOrNull<Verify2FARequest>() ?: throw UserMissing2FACodeException()
                authService.verify2FA(verifyValues)
                val userResponseWithToken = user.toUserResponseWith2FAToken()
                call.respond(SuccessResponse(data = userResponseWithToken.user, token = userResponseWithToken.token))
            }
        }
    }
}

data class RegisterRequest(val email: String, val password: String, val confirmPassword: String)

data class LoginRequest(val email: String, val password: String)

data class Verify2FARequest(val code: String?)
