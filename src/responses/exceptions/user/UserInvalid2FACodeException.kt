package club.pengubank.responses.exceptions.user

import club.pengubank.errors.exceptions.UnauthorizedException

class UserInvalid2FACodeException : UnauthorizedException("The code you have provided is invalid")
