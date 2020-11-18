package club.pengubank.errors.exceptions

import io.ktor.http.*

abstract class PenguBankException(override val message: String, val status: HttpStatusCode) : RuntimeException(message)