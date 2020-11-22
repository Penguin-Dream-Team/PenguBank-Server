package responses.exceptions.user

import responses.exceptions.NotFoundException

data class UserNotFoundException(val id: Int) : NotFoundException("User with id '$id' not found")
