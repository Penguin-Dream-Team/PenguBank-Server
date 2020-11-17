package club.pengubank.errors.exceptions.user

import club.pengubank.errors.exceptions.NotFoundException

data class UserNotFoundException(val id: Int) : NotFoundException("User with id '$id' not found")
