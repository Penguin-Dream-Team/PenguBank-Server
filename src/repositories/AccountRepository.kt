package repositories

import responses.exceptions.account.AccountNotFoundException
import models.*
import org.jetbrains.exposed.sql.transactions.transaction

class AccountRepository {

    fun getAllAccounts(): Iterable<AccountResponse> = transaction {
        AccountEntity.all().map(AccountEntity::toAccountResponse)
    }

    fun getAccount(accountId: Int) = transaction {
        AccountEntity.findById(accountId) ?: throw AccountNotFoundException(accountId)
    }
}