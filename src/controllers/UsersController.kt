package controllers

import application.UserGet
import application.UserList
import application.user
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import models.User
import org.kodein.di.instance
import org.kodein.di.ktor.di
import responses.SuccessResponse
import services.UserService

@KtorExperimentalLocationsAPI
fun Route.users() {
    val userService by di().instance<UserService>()

    authenticate("password-2fauth", optional = true) {
        get<UserList> {
            val allUsers = userService.getAllUsers().map(User::toSimpleUserResponse)
            println(call.user?.token)
            println(call.user == null)
            call.respond(SuccessResponse(data = allUsers, token = call.user?.token))
        }
    }
}
