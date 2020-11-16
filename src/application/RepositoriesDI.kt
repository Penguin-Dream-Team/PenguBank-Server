package club.pengubank.application

import club.pengubank.repositories.UserRepository
import club.pengubank.services.UserService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun DI.MainBuilder.bindServices() {
    bind<UserService>() with singleton { UserService(UserRepository()) }
    // bind<AccountService>() with singleton { AccountService() }
}