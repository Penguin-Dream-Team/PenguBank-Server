package responses.exceptions.user

import responses.exceptions.NotFoundException

data class UserWrongEmailException(val email: String) : NotFoundException("User with email '$email' not found")
