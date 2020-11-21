package club.pengubank.application

import club.pengubank.errors.exceptions.PenguBankException
import club.pengubank.responses.ErrorResponse
import club.pengubank.services.AuthService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import org.kodein.di.instance
import org.kodein.di.ktor.di
import org.slf4j.event.Level
import java.text.DateFormat

fun Application.installFeatures() {
    val authService by di().instance<AuthService>()

    install(Locations)

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    /*
        install(CORS) {
            method(HttpMethod.Options)
            method(HttpMethod.Put)
            method(HttpMethod.Delete)
            method(HttpMethod.Patch)
            header(HttpHeaders.Authorization)
            header("MyCustomHeader")
            allowCredentials = true
            anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
        }
    */

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(Authentication) {
        jwt("password-auth") {
            verifier(JWTAuthenticationConfig.verifier)
            realm = JWTAuthenticationConfig.issuer
            validate { authService.validateUserJWT(it) }
        }

        jwt("password-2fauth") {
            verifier(JWTAuthenticationConfig.verifier)
            realm = JWTAuthenticationConfig.issuer
            validate { authService.validateUser2FAJWT(it) }
        }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            setDateFormat(DateFormat.LONG)
        }
    }

    install(StatusPages) {
        exception<Throwable> { e ->

            val status = when (e) {
                is PenguBankException -> e.status
                else -> HttpStatusCode.InternalServerError
            }

            call.respond(status, ErrorResponse(status = status.toString(), message = e.message.toString()))
        }

        status(HttpStatusCode.UnsupportedMediaType) { status ->
            call.respond(
                status,
                ErrorResponse(
                    status = status.toString(),
                    message = "Request body needs to be of type 'application/json'"
                )
            )
        }

        status(HttpStatusCode.NotFound) { status ->
            call.respond(status, ErrorResponse(status = status.toString(), message = "Requested resource not found."))
        }

        status(HttpStatusCode.Unauthorized) { status ->
            call.respond(
                status,
                ErrorResponse(status = status.toString(), message = "You need to be logged in to access this resource.")
            )
        }
    }
}