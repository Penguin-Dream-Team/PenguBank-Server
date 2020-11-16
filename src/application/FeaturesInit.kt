package club.pengubank.application

import club.pengubank.errors.ErrorResponse
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.slf4j.event.Level
import java.text.DateFormat

fun Application.installFeatures() {
    install(Locations) { }

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

    //install(Authentication) { }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            setDateFormat(DateFormat.LONG)
        }
    }

    install(StatusPages) {
        exception<Throwable> { e ->

            val status = when (e) {
                is EntityNotFoundException, is NotFoundException -> HttpStatusCode.NotFound
                else -> HttpStatusCode.InternalServerError
            }

            call.respond(status, ErrorResponse(status = status.toString(), message = e.message.toString()))
        }
    }

}