package com.mehdiflix.mehdiflix.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.Views
import com.mehdiflix.mehdiflix.domain.*
import com.mehdiflix.mehdiflix.services.SeriesService
import com.mehdiflix.mehdiflix.services.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestControllerAdvice
@RestController
// @Cors
@RequestMapping("/users")
class UserController(val us: UserService) {

    @Operation(summary = "Get user with username or with id. Only one of them should be provided")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "OK: User found",
            content = [Content(
                schema = Schema(implementation = User::class),
                mediaType = "application/json"
            )]
        ),
        ApiResponse(
            responseCode = "400",
            description = "BAD_REQUEST: The username and id are not provided or both are provided",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "NOT_FOUND: User not found",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
    ])
    @GetMapping("") @JsonView(Views.GetUser::class)
    fun getUser(
        @RequestParam(required = false)
        username: String?,
        @RequestParam(required = false)
        id: Long?
    ): ResponseEntity<User> {

        if ((username == null && id == null) || (username != null && id != null))
            return ResponseEntity.badRequest().build()

        try {
            val user = if (username != null)
                us.getUser(username)
            else
                us.getUser(id!!)
            return ResponseEntity.ok(user)
        } catch (_: UserService.NoUserWithUserNameException) {
        } catch (_: UserService.NoUserWithIdException) {}
        return ResponseEntity.notFound().build()
    }

    @Operation(summary = "Add series to user personal space")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "OK: Series added successfully",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "NOT_FOUND: User or series not found",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
    ])
    @PutMapping("/{userId}/addedSeries/{seriesId}")
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

    @Operation(summary = "Gets the bills of the user")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "OK: user and its bills found",
            content = [Content(
                array = ArraySchema(schema = Schema(implementation = Bill::class)),
                mediaType = "application/json"
            )]
        ),
        ApiResponse(
            responseCode = "404",
            description = "NOT_FOUND: User not found",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
    ])
    @GetMapping("/{userId}/bills") @JsonView(Views.GetBills::class)
    fun getBills(@PathVariable userId: Long): ResponseEntity<Set<Bill>> {
        try {
            val bills = us.getUserBills(userId)
            return ResponseEntity.ok(bills)
        } catch (_: UserService.NoUserWithIdException) {
            return ResponseEntity.notFound().build()
        }
    }

    @Operation(summary = "Add view of the episode from the series to user")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "OK: View added successfully",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "NOT_FOUND: User or series not found or series does not have the season or the episode",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
        ApiResponse(
            responseCode = "403",
            description = "FORBIDEN: Series has not been added to user personal space",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
    ])
    @PutMapping("/{userId}/addedSeries/{seriesId}/seasons/{seasonNumber}/episodes/{episodeNumber}")
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
        } catch (_: SeriesNotAddedException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        } catch (_: UserService.NoUserWithIdException) {
        } catch (_: SeriesService.NoSeriesWithIdException) {
        } catch (_: SeasonNumberNotInSeriesException) {
        } catch (_: EpisodeNumberNotInSeasonException) {}
        return ResponseEntity.notFound().build()
    }
}
