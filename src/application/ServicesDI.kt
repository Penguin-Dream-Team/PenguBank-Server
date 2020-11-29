package application

import repositories.AccountRepository
import repositories.TransactionRepository
import repositories.QueuedTransactionRepository
import repositories.UserRepository
import services.AccountService
import services.AuthService
import services.TransactionService
import services.UserService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun DI.MainBuilder.bindServices() {
    bind<AuthService>() with singleton { AuthService(UserRepository()) }
    bind<UserService>() with singleton { UserService(UserRepository()) }
    bind<AccountService>() with singleton { AccountService(AccountRepository()) }
    bind<TransactionService>() with singleton { TransactionService(TransactionRepository(), QueuedTransactionRepository()) }
}
