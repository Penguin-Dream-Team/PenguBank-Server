package responses.exceptions.account

import responses.exceptions.NotFoundException

data class AccountNotFoundException(val id: Int) : NotFoundException("Account with id '$id' not found")
