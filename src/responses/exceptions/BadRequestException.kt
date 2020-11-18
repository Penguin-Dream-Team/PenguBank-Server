package club.pengubank.errors.exceptions

import io.ktor.http.*

abstract class BadRequestException(override val message: String) : PenguBankException(message, HttpStatusCode.BadRequest)
