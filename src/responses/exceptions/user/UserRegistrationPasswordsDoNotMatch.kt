package responses.exceptions.user

import responses.exceptions.BadRequestException

class UserRegistrationPasswordsDoNotMatch :
    BadRequestException("The passwords provided do not match")
