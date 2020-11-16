package club.pengubank.controllers

import club.pengubank.application.UserGet
import club.pengubank.application.UserList
import club.pengubank.services.UserService
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.users() {
    val userService by di().instance<UserService>()

    get<UserList> {
        val allUsers = userService.getAllUsers()
        call.respond(allUsers)
    }

    get<UserGet> { userGet ->
        val user = userService.getUserById(userGet.id)
        call.respond(user.toUser())
    }
}