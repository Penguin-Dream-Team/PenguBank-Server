package responses

data class ErrorResponse(val status: String, val statusCode: String = status.split(" ")[0], val message: String)

data class SuccessResponse(val status: String = "success", val statusCode: String = "200", val token: String? = null, val data: Any)
