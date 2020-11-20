package club.pengubank.application

import club.pengubank.repositories.AccountRepository
import club.pengubank.repositories.TransactionRepository
import club.pengubank.repositories.UserRepository
import club.pengubank.services.AccountService
import club.pengubank.services.AuthService
import club.pengubank.services.TransactionService
import club.pengubank.services.UserService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun DI.MainBuilder.bindServices() {
    bind<AuthService>() with singleton { AuthService(UserRepository()) }
    bind<UserService>() with singleton { UserService(UserRepository()) }
    bind<AccountService>() with singleton { AccountService(AccountRepository()) }
    bind<TransactionService>() with singleton { TransactionService(TransactionRepository()) }

}