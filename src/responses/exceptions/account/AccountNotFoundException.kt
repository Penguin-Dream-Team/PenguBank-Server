package responses.exceptions.account

import responses.exceptions.NotFoundException
import javax.naming.InsufficientResourcesException

data class AccountNotFoundException(val id: Int) : NotFoundException("Account with id '$id' not found")

data class AccountNotEnoughBalanceException(val id: Int) : InsufficientResourcesException("You do not have enough balance to perform the transaction")
