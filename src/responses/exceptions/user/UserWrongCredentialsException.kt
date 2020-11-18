package club.pengubank.responses.exceptions.user

import club.pengubank.errors.exceptions.UnauthorizedException

class UserWrongCredentialsException : UnauthorizedException("Login failed, wrong credentials")
