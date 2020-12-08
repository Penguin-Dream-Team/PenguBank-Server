package responses.exceptions.user

import responses.exceptions.NotFoundException

class UserPhoneNotYetRegisteredException : NotFoundException("This account has not registered a phone yet")
