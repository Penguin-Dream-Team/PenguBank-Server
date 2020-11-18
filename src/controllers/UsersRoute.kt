package club.pengubank.controllers

import club.pengubank.application.UserGet
import club.pengubank.application.UserList
import club.pengubank.application.user
import club.pengubank.models.User
import club.pengubank.models.UserEntity
import club.pengubank.responses.SuccessResponse
import club.pengubank.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.users() {
    val userService by di().instance<UserService>()

    authenticate("password-2fauth", optional = true) {
        get<UserList> {
            val allUsers = userService.getAllUsers().map(User::toUserResponse)
            println(call.user?.token)
            println(call.user == null)
            call.respond(SuccessResponse(data = allUsers, token = call.user?.token))
        }

        get<UserGet> { userGet ->
            val user = userService.getUserById(userGet.id).toUserResponse()
            call.respond(SuccessResponse(data = user, token = call.user?.token))
        }
    }
}