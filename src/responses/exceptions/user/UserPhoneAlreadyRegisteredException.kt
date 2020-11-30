package club.pengubank.responses.exceptions.user

import responses.exceptions.NotFoundException

class UserPhoneAlreadyRegisteredException : NotFoundException("This account has already registered a phone")
