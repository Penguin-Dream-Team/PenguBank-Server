package club.pengubank.controllers

import club.pengubank.application.About
import club.pengubank.application.Home
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@KtorExperimentalLocationsAPI
fun Route.webPages() {
    location<Home> {
        get {
            call.respondText("Hello penguins")
        }
    }
}