package responses.exceptions.user

import responses.exceptions.BadRequestException

data class UserRegistrationDuplicateEmailException(val email: String) : BadRequestException("User with email '$email' already found")
