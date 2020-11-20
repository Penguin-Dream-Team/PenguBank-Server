package club.pengubank.services

import club.pengubank.models.Account
import club.pengubank.models.AccountEntity
import club.pengubank.repositories.AccountRepository

class AccountService(private val accountRepository: AccountRepository) {

    fun getAllAccounts(): Iterable<Account> = accountRepository.getAllAccounts()

    fun getAccountById(accountId: Int): AccountEntity = accountRepository.getAccount(accountId)
}