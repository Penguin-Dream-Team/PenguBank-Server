package responses

data class ErrorResponse(val message: String)

data class SuccessResponse(val data: Any? = null, val token: String? = null)
