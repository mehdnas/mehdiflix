package com.mehdiflix.mehdiflix.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.Views
import com.mehdiflix.mehdiflix.domain.*
import com.mehdiflix.mehdiflix.services.SeriesService
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
        @RequestBody @JsonView(Views.Public::class, Views.Private::class)
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

    @GetMapping("") @JsonView(Views.WithId::class)
    fun getUser(
        @RequestParam(required = false)
        username: String?,
        @RequestParam(required = false)
        userId: Long?
    ): ResponseEntity<User> {

        if ((username == null && userId == null) || (username != null && userId != null))
            return ResponseEntity.badRequest().build()

        try {
            val user = if (username != null)
                us.getUser(username)
            else
                us.getUser(userId!!)
            return ResponseEntity.ok(user)
        } catch (_: UserService.NoUserWithUserNameException) {
            return ResponseEntity.notFound().build()
        } catch (_: UserService.NoUserWithIdException) {
            return ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{userId}/addedSeries") @JsonView(Views.WithId::class)
    fun addedSeriesOfUser(@PathVariable userId: Long): ResponseEntity<Set<Series>> {
        try {
            val series = us.getAddedSeriesOfUser(userId)
            return ResponseEntity.ok(series)
        } catch (_: UserService.NoUserWithIdException) {
            return ResponseEntity.notFound().build()
        }
    }


    @PostMapping("/{userId}/addedSeries/{seriesId}")
    fun addSeriesToUser(
        @PathVariable userId: Long, @PathVariable seriesId: Long
    ): ResponseEntity<Unit> {
        try {
            us.addSeriesToUser(userId, seriesId)
            return ResponseEntity.ok().build()
        } catch (_: UserService.NoUserWithIdException) {
        } catch (_: SeriesService.NoSeriesWithIdException) {}
        return ResponseEntity.notFound().build()
    }


    @GetMapping("/{userId}/bills") @JsonView(Views.WithId::class)
    fun getBills(@PathVariable userId: Long): ResponseEntity<Set<Bill>> {
        try {
            val bills = us.getUserBills(userId)
            return ResponseEntity.ok(bills)
        } catch (_: UserService.NoUserWithIdException) {
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{userId}/addedSeries/{seriesId}/seasons/{seasonNumber}/episodes/{episodeNumber}")
    fun addViewToUser(
        @PathVariable
        userId: Long,
        @PathVariable
        seriesId: Long,
        @PathVariable
        seasonNumber: Int,
        @PathVariable
        episodeNumber: Int
    ): ResponseEntity<Unit> {
        try {
            us.addViewToUser(seriesId, userId, seasonNumber, episodeNumber)
            return ResponseEntity.ok().build()
        } catch (_: UserService.NoUserWithIdException) {
        } catch (_: SeriesService.NoSeriesWithIdException) {
        } catch (_: SeasonNumberNotInSeriesException) {
        } catch (_: EpisodeNumberNotInSeasonException) {
        } catch (_: SeriesNotAddedException) {}
        return ResponseEntity.notFound().build()
    }
}
