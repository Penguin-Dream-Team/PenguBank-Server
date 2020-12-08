package controllers

import application.Dashboard
import application.Activate2FA
import application.UserMobileKey
import application.user
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
import responses.SuccessResponse
import responses.exceptions.user.UserMissing2FACodeException
import services.AccountService
import services.AuthService
import services.UserService
import totp.QRCodeFactory

@KtorExperimentalLocationsAPI
fun Route.dashboard() {
    val accountService by di().instance<AccountService>()
    val userService by di().instance<UserService>()
    val authService by di().instance<AuthService>()

    authenticate("password-2fauth") {
        get<Dashboard> {
            withContext(Dispatchers.IO) {
                val userWithToken = call.user!!
                val account = accountService.getAccountById(userWithToken.user.accountId)
                call.respond(SuccessResponse(data = account.toAccountResponse(), token = userWithToken.token))
            }
        }

        get<UserMobileKey> {
            withContext(Dispatchers.IO) {
                val userWithToken = call.user!!
                val user = userService.getUserById(userWithToken.user.id)
                call.respond(
                    SuccessResponse(
                        data = mapOf("phonePublicKey" to user.phonePublicKey),
                        token = userWithToken.token
                    )
                )
            }
        }

        location<Activate2FA> {
            post {
                withContext(Dispatchers.IO) {
                    val userWithToken = call.user!!
                    val user = userService.getUserById(userWithToken.user.id)

                    val url = QRCodeFactory.createQRCodeURL("PenguBank Inc", user.email, user.getSecretKey())

                    call.respond(SuccessResponse(data = mapOf("qrcode" to url), token = userWithToken.token))
                }
            }

            put {
                withContext(Dispatchers.IO) {
                    val userWithToken = call.user!!
                    val user = userService.getUserById(userWithToken.user.id)

                    val verifyValues = call.receiveOrNull<Verify2FARequest>() ?: throw UserMissing2FACodeException()
                    authService.verify2FA(user.id!!, verifyValues)

                    val newUser = userService.activate2FA(user.id)

                    val userResponseWithToken = newUser.toUserResponseWith2FAToken()
                    call.respond(
                        SuccessResponse(
                            data = userResponseWithToken.user,
                            token = userResponseWithToken.token
                        )
                    )
                }
            }
        }
    }
}

