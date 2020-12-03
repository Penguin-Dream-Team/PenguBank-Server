package controllers

import application.Home
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.locations.*
import io.ktor.routing.*
import kotlinx.html.*

@KtorExperimentalLocationsAPI
fun Route.webPages() {
    location<Home> {
        get {
            call.respondHtml {
                head {
                    meta(charset = "UTF-8")
                    title { +"PenguBank" }
                }
                body {
                    h1 {
                        +"Pengu Banking Solutions | Coming Soon"
                    }
                }
            }
        }
    }
}
