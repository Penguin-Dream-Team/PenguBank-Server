package responses.exceptions

import io.ktor.http.*

abstract class NotFoundException(override val message: String) : PenguBankException(message, HttpStatusCode.NotFound)
