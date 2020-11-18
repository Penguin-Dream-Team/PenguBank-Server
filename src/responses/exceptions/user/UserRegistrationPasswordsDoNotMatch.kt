package club.pengubank.responses.exceptions.user

import club.pengubank.errors.exceptions.BadRequestException

class UserRegistrationPasswordsDoNotMatch :
    BadRequestException("The passwords provided do not match")
