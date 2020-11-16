package club.pengubank.errors

data class ErrorResponse(val status: String, val message: String)

class UnauthorizedException(message:String): Exception(message)