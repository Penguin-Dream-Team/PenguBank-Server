package responses.exceptions.user

import responses.exceptions.UnauthorizedException

class UserInvalid2FACodeException : UnauthorizedException("The code you have provided is invalid")
