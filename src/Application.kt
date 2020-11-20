package club.pengubank

import club.pengubank.application.bindServices
import club.pengubank.application.initDatabase
import club.pengubank.application.installFeatures
import club.pengubank.controllers.accounts
import club.pengubank.controllers.login
import club.pengubank.controllers.users
import club.pengubank.controllers.webPages
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.util.*
import org.kodein.di.ktor.di

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun Application.module(testing: Boolean = false) {

    initDatabase()

    installFeatures()

    di {
        bindServices()
    }

    routing {
        webPages()
        users()
        login()
        accounts()
    }
}
