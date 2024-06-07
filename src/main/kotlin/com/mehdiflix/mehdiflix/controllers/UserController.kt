package com.mehdiflix.mehdiflix.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.Views
import com.mehdiflix.mehdiflix.domain.*
import com.mehdiflix.mehdiflix.services.SeriesService
import com.mehdiflix.mehdiflix.services.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(
    name = "User API",
    description = "Management of personal space of users, their views and bills.",
)
@RestControllerAdvice
@RestController
@CrossOrigin(origins=["http://localhost:4200"])
@RequestMapping("/users")
class UserController(val us: UserService) {

    @Operation(
        summary = "Get user with username or with id.",
        description = "Gets the user with the username or id provided as parameter. Only one of them should be provided.",
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "OK: User found.",
            content = [Content(
                schema = Schema(implementation = User::class),
                mediaType = "application/json"
            )]
        ),
        ApiResponse(
            responseCode = "400",
            description = "BAD_REQUEST: The username and id are not provided or both are provided.",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "NOT_FOUND: User not found.",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
    ])
    @GetMapping("") @JsonView(Views.GetUser::class)
    fun getUser(

        @Parameter(description = "Username of the user.")
        @RequestParam(required = false)
        username: String?,

        @Parameter(description = "Id of the user.")
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
        } catch (_: UserService.NoUserWithIdException) {}
        return ResponseEntity.notFound().build()
    }

    @Operation(
        summary = "Add series to user personal space.",
        description = "Adds the series with the provided id as parameter to the personal space of the user whose id" +
                " is provided as parameter.",
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "OK: Series added successfully.",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "NOT_FOUND: User or series not found.",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
    ])
    @PutMapping("/{userId}/addedSeries/{seriesId}")
    fun addSeriesToUser(

        @Parameter(description = "Id of the user.")
        @PathVariable
        userId: Long,

        @Parameter(description = "Id of the series.")
        @PathVariable
        seriesId: Long

    ): ResponseEntity<Unit> {
        try {
            us.addSeriesToUser(userId, seriesId)
            return ResponseEntity.ok().build()
        } catch (_: UserService.NoUserWithIdException) {
        } catch (_: SeriesService.NoSeriesWithIdException) {}
        return ResponseEntity.notFound().build()
    }

    @Operation(
        summary = "Get bills of user.",
        description = "Gets the bills of the user whose id is provided as parameter.",
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "OK: user and its bills found.",
            content = [Content(
                array = ArraySchema(schema = Schema(implementation = Bill::class)),
                mediaType = "application/json."
            )]
        ),
        ApiResponse(
            responseCode = "404",
            description = "NOT_FOUND: User not found.",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
    ])
    @GetMapping("/{userId}/bills") @JsonView(Views.GetBills::class)
    fun getBills(

        @Parameter(description = "Id of the user.")
        @PathVariable
        userId: Long

    ): ResponseEntity<Set<Bill>> {
        try {
            val bills = us.getUserBills(userId)
            return ResponseEntity.ok(bills)
        } catch (_: UserService.NoUserWithIdException) {
            return ResponseEntity.notFound().build()
        }
    }

    @Operation(
        summary = "Add view of episode to user.",
        description = "Adds a view of the episode from the indicated season of the series to the user.",
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "OK: View added successfully.",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "NOT_FOUND: User or series not found or series does not have the season or the episode.",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
        ApiResponse(
            responseCode = "403",
            description = "FORBIDEN: Series has not been added to user personal space.",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
    ])
    @PutMapping("/{userId}/addedSeries/{seriesId}/seasons/{seasonNumber}/episodes/{episodeNumber}")
    fun addViewToUser(

        @Parameter(description = "Id of the user.")
        @PathVariable
        userId: Long,

        @Parameter(description = "Id of the series.")
        @PathVariable
        seriesId: Long,

        @Parameter(description = "Number of the season.")
        @PathVariable
        seasonNumber: Int,

        @Parameter(description = "Number of the episode.")
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
