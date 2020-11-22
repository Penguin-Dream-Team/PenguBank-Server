package club.pengubank

import application.bindServices
import application.initDatabase
import application.installFeatures
import controllers.accounts
import controllers.dashboard
import controllers.login
import controllers.users
import controllers.webPages
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
        dashboard()
    }
}
