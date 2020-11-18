package club.pengubank.responses.exceptions.user

import club.pengubank.errors.exceptions.BadRequestException

class UserAlreadyLoggedInException :
    BadRequestException("You cannot access this resource because you are already logged in")
