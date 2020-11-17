package club.pengubank.errors.exceptions.user

import club.pengubank.errors.exceptions.UnauthorizedException

class UserWrongCredentialsException : UnauthorizedException("Login failed, wrong credentials")
