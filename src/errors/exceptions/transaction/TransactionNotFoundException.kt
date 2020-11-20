package club.pengubank.errors.exceptions.transaction

import club.pengubank.errors.exceptions.NotFoundException

data class TransactionNotFoundException(val id: Int) : NotFoundException("Transaction with id '$id' not found")
