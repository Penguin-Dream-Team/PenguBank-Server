package club.pengubank.responses.exceptions.user

import club.pengubank.errors.exceptions.NotFoundException

data class UserWrongEmailException(val email: String) : NotFoundException("User with email '$email' not found")
