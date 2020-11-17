package club.pengubank.controllers

import club.pengubank.application.Dashboard
import club.pengubank.application.user
import club.pengubank.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.dashboard() {
    val userService by di().instance<UserService>()

    authenticate("password-auth") {
        get<Dashboard> {
            val user = call.user!!
            call.respond(user)
        }
    }
}