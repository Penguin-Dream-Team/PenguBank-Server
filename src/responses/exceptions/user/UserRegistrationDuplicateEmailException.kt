package club.pengubank.responses.exceptions.user

import club.pengubank.errors.exceptions.BadRequestException

data class UserRegistrationDuplicateEmailException(val email: String) : BadRequestException("User with email '$email' already found")
