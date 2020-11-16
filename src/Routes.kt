package club.pengubank

import io.ktor.locations.*

@KtorExperimentalLocationsAPI
@Location("/")
class Home

@KtorExperimentalLocationsAPI
@Location("/about")
class About


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