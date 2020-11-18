package club.pengubank.application

import io.ktor.locations.*

@KtorExperimentalLocationsAPI
@Location("/")
class Home

@KtorExperimentalLocationsAPI
@Location("/about")
class About

/* User Routes */

@KtorExperimentalLocationsAPI
@Location("/users")
class UserList

@KtorExperimentalLocationsAPI
@Location("/users/{id}")
data class UserGet(val id: Int)

@KtorExperimentalLocationsAPI
@Location("/register")
class RegisterUser

@KtorExperimentalLocationsAPI
@Location("/login")
class LoginUser

@KtorExperimentalLocationsAPI
@Location("/verify")
class Verify2FA

@KtorExperimentalLocationsAPI
@Location("/dashboard")
class Dashboard

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