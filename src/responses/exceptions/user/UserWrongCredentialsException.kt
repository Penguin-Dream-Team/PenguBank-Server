package responses.exceptions.user

import responses.exceptions.UnauthorizedException

class UserWrongCredentialsException : UnauthorizedException("Login failed, wrong credentials")
