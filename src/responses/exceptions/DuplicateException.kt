package responses.exceptions

import io.ktor.http.*

abstract class DuplicateException(override val message: String) : PenguBankException(message, HttpStatusCode.Conflict)
