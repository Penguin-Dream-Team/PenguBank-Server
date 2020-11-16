package club.pengubank.routes

import club.pengubank.About
import club.pengubank.Home
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

    location<About> {
        get {
            call.respondText { "About me" }
        }
    }
}