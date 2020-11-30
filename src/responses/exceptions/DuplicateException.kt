package club.pengubank.responses.exceptions

import io.ktor.http.*
import responses.exceptions.PenguBankException

abstract class DuplicateException(override val message: String) : PenguBankException(message, HttpStatusCode.Conflict)
