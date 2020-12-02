package repositories

import responses.exceptions.account.AccountNotFoundException
import models.*
import org.jetbrains.exposed.sql.transactions.transaction
import responses.exceptions.account.AccountNotEnoughBalanceException

class AccountRepository {

    fun getAllAccounts(): Iterable<AccountResponse> = transaction {
        AccountEntity.all().map(AccountEntity::toAccountResponse)
    }

    fun getAccount(accountId: Int) = transaction {
        AccountEntity.findById(accountId) ?: throw AccountNotFoundException(accountId)
    }

    fun addBalance(accountId: Int, amount: Int) = transaction {
        val account = getAccount(accountId)
        account.balance += amount
    }

    fun removeBalance(accountId: Int, amount: Int) = transaction {
        val account = getAccount(accountId)

        if (account.balance < amount) throw AccountNotEnoughBalanceException(accountId)

        account.balance -= amount
    }
}