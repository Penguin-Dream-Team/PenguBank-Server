package responses.exceptions.transaction

import responses.exceptions.NotFoundException

data class AccountTransactionsNotFoundException(val id: Int) : NotFoundException("Transactions for account with id '$id' not found")
