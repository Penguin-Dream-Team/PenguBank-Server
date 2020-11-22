package responses.exceptions

import io.ktor.http.*

abstract class UnauthorizedException(override val message: String) : PenguBankException(message, HttpStatusCode.Unauthorized)