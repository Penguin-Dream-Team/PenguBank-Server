package club.pengubank.responses.exceptions.transaction

import responses.exceptions.BadRequestException

class TransactionTokenInvalidException : BadRequestException("The provided token is invalid")
