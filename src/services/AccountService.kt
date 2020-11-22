package services

import models.AccountResponse
import models.AccountEntity
import repositories.AccountRepository

class AccountService(private val accountRepository: AccountRepository) {

    fun getAllAccounts(): Iterable<AccountResponse> = accountRepository.getAllAccounts()

    fun getAccountById(accountId: Int): AccountEntity = accountRepository.getAccount(accountId)
}