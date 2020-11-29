package application

import io.ktor.locations.*

@KtorExperimentalLocationsAPI
@Location("/")
class Home

@KtorExperimentalLocationsAPI
@Location("/users")
class UserList

@KtorExperimentalLocationsAPI
@Location("/register")
class RegisterUser

@KtorExperimentalLocationsAPI
@Location("/login")
class LoginUser

@KtorExperimentalLocationsAPI
@Location("/setup")
class SetupPhone

@KtorExperimentalLocationsAPI
@Location("/verify")
class Verify2FA

@KtorExperimentalLocationsAPI
@Location("/dashboard")
class Dashboard

@KtorExperimentalLocationsAPI
@Location("/activate")
class Activate2FA

@KtorExperimentalLocationsAPI
@Location("/accounts")
class Accounts

@KtorExperimentalLocationsAPI
@Location("/transactions")
class Transactions

/*
    - Home => optional

    - Login
    - Logout

    - Transaction:
        - List
        - Create

    - Account:
        - View
 */