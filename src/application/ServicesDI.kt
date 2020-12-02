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

    val userRepository = UserRepository()
    val accountRepository = AccountRepository()
    val transactionRepository = TransactionRepository(accountRepository)
    val queuedTransactionRepository = QueuedTransactionRepository(accountRepository, transactionRepository)

    bind<AuthService>() with singleton { AuthService(userRepository) }
    bind<UserService>() with singleton { UserService(userRepository) }
    bind<AccountService>() with singleton { AccountService(accountRepository) }
    bind<TransactionService>() with singleton { TransactionService(transactionRepository, queuedTransactionRepository) }
}
