package responses.exceptions.transaction

import responses.exceptions.BadRequestException

class InvalidTransactionAmountException : BadRequestException("The amount needs to be a positive number")
