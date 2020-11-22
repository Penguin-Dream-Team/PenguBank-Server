package responses.exceptions.user

import responses.exceptions.BadRequestException

class UserAlreadyLoggedInException :
    BadRequestException("You cannot access this resource because you are already logged in")
