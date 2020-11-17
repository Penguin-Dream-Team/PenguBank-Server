package club.pengubank.errors.exceptions

import io.ktor.http.*

abstract class NotFoundException(override val message: String) : PenguBankException(message, HttpStatusCode.NotFound)