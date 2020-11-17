package club.pengubank.errors

data class ErrorResponse(val status: String, val statusCode: String = status.split(" ")[0], val message: String)
