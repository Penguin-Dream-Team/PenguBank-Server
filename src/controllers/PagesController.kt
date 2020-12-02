package controllers

import application.Home
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

@KtorExperimentalLocationsAPI
fun Route.webPages() {
    location<Home> {
        get {
            call.respondFile(File("resources/pages/index.html"))
        }
    }
}
