package responses.exceptions.user

import responses.exceptions.BadRequestException

class UserMissing2FACodeException :
    BadRequestException("You need to provide a valid code for the 2FA verification process")
