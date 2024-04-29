package com.mehdiflix.mehdiflix.controllers

import com.mehdiflix.mehdiflix.domain.User
import com.mehdiflix.mehdiflix.services.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(val us: UserService) {

    @PutMapping("", consumes = ["application/json"])
    fun addUser(@RequestBody user: User) = us.addUser(user)

    @GetMapping("")
    fun getAllUsers() = us.getAllUsers()

    @GetMapping("/{username}")
    fun getUser(@PathVariable username: String) = us.getUser(username)
}
