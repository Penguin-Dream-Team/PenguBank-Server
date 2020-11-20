package club.pengubank.errors.exceptions.account

import club.pengubank.errors.exceptions.NotFoundException

data class AccountNotFoundException(val id: Int) : NotFoundException("Account with id '$id' not found")
