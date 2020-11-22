package responses.exceptions.transaction

import responses.exceptions.NotFoundException

data class TransactionNotFoundException(val id: Int) : NotFoundException("Transaction with id '$id' not found")
