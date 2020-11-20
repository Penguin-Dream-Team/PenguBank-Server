package club.pengubank.repositories

import club.pengubank.errors.exceptions.account.AccountNotFoundException
import club.pengubank.models.*
import org.jetbrains.exposed.sql.transactions.transaction

class AccountRepository {

    fun getAllAccounts(): Iterable<Account> = transaction {
        AccountEntity.all().map(AccountEntity::toAccount)
    }

    fun getAccount(accountId: Int) = transaction {
        AccountEntity.findById(accountId) ?: throw AccountNotFoundException(accountId)
    }

    fun addAccount(account: Account) = transaction {
        AccountEntity.new {
            this.balance = account.balance
        }
    }

    fun deleteAccount(accountId: Int) = transaction {
        val account = getAccount(accountId)
        account.delete()
    }
}