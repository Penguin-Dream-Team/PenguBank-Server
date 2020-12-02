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
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import org.kodein.di.instance
import org.kodein.di.ktor.di
import services.TransactionService
import java.time.Duration

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

    launch {
        while (true) {
            val transactionService by di().instance<TransactionService>()
            transactionService.performExpiredTransactionsCronJob()
            delay(Duration.ofMinutes(10))
        }
    }
}
