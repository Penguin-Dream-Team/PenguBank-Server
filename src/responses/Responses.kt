package responses

data class ErrorResponse(val message: String)

data class SuccessResponse(val token: String? = null, val data: Any? = null)
