package responses.exceptions.user

import responses.exceptions.DuplicateException

class UserPhoneAlreadyRegisteredException : DuplicateException("This account has already registered a phone")
