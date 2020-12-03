package responses.exceptions.transaction

import responses.exceptions.BadRequestException

class CannotSendTransactionToSelfException : BadRequestException("You can't queue a transaction to your own account")
