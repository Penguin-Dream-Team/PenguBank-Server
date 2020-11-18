package club.pengubank.application

import club.pengubank.repositories.UserRepository
import club.pengubank.services.AuthService
import club.pengubank.services.UserService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun DI.MainBuilder.bindServices() {
    bind<AuthService>() with singleton { AuthService(UserRepository()) }
    bind<UserService>() with singleton { UserService(UserRepository()) }
}