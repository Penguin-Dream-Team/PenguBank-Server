package club.pengubank.errors.exceptions.transaction

import club.pengubank.errors.exceptions.NotFoundException

data class AccountTransactionsNotFoundException(val id: Int) : NotFoundException("Transactions for account with id '$id' not found")
