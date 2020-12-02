package controllers

import application.*
import responses.exceptions.user.UserMissing2FACodeException
import responses.SuccessResponse
import responses.exceptions.user.UserAlreadyLoggedInException
import services.AuthService
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
import responses.ErrorResponse
import services.UserService
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.ErrorReporter

@KtorExperimentalLocationsAPI
fun Route.login() {
    val authService by di().instance<AuthService>()
    val userService by di().instance<UserService>()

    post<RegisterUser> {
        if (!call.guest) throw UserAlreadyLoggedInException()

        withContext(Dispatchers.IO) {
            val registerValues = call.receive<RegisterRequest>()
            val newUser = authService.register(registerValues)
            call.respond(SuccessResponse(data = newUser.toSimpleUserResponse()))
        }
    }

    post<LoginUser> {
        if (!call.guest) throw UserAlreadyLoggedInException()

        withContext(Dispatchers.IO) {
            val loginValues = call.receive<LoginRequest>()
            val loggedUser = authService.login(loginValues.email, loginValues.password)
            val token =
                if (loggedUser.enabled2FA)
                    loggedUser.toUserResponseWithToken().token
                else
                    loggedUser.toUserResponseWith2FAToken().token

            call.respond(SuccessResponse(data = loggedUser.toSimpleUserResponse(), token = token))
        }
    }

    authenticate("password-2fauth") {
        post<SetupPhone> {
            val loggedUser = call.user!!

            withContext(Dispatchers.IO) {
                val setupValues = call.receive<SetupRequest>()
                userService.setupPhone(loggedUser.user, setupValues.phonePublicKey)

                call.respond(SuccessResponse(data = loggedUser))
            }
        }
    }

    authenticate("password-auth") {
        post<Verify2FA> {
            withContext(Dispatchers.IO) {
                val user = call.tempUser!!
                val verifyValues = call.receiveOrNull<Verify2FARequest>() ?: throw UserMissing2FACodeException()
                authService.verify2FA(user.user.id, verifyValues)
                val userResponseWithToken = user.toUserResponseWith2FAToken()
                call.respond(SuccessResponse(data = userResponseWithToken.user, token = userResponseWithToken.token))
            }
        }
    }
}

data class RegisterRequest(val email: String, val password: String, val confirmPassword: String)
data class LoginRequest(val email: String, val password: String)
data class SetupRequest(val phonePublicKey: String)
data class Verify2FARequest(val code: Int?)
