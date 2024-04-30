package com.mehdiflix.mehdiflix.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.Views.NewUser
import com.mehdiflix.mehdiflix.domain.User
import com.mehdiflix.mehdiflix.services.UserService
import com.mehdiflix.mehdiflix.services.UserService.UsernameAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/users")
class UserController(val us: UserService) {

    @PutMapping("", consumes = ["application/json"])
    fun addUser(
        @RequestBody @JsonView(NewUser::class)
        user: User
    ): ResponseEntity<User> {
        val added: User
        try {
            added = us.addUser(user)
        } catch (_: UsernameAlreadyExistsException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val resourceLink = URI("/users/${added.username}")
        return ResponseEntity.created(resourceLink).build()
    }

//    @GetMapping("/{username}")
//    fun getUser()

}
