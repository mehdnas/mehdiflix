package com.mehdiflix.mehdiflix.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.Views
import com.mehdiflix.mehdiflix.services.SeriesService
import com.mehdiflix.mehdiflix.domain.Series
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(
    name = "Series API",
    description = "Series retrieval"
)
@RestControllerAdvice
@CrossOrigin(origins=["http://localhost:4200"])
@RestController
// @Cors
@RequestMapping("/series")
class SeriesController(val ss: SeriesService) {

    @Operation(
        summary = "Search series using title beginning.",
        description = "Searches for series whose titles start with the string provided as parameter (titleBeginning). " +
                "It is case insensitive."
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(
                mediaType = "application/json",
                array = ArraySchema(schema = Schema(implementation = Series::class))
            )],
        ),
        ApiResponse(
            responseCode = "400",
            description = "BAD_REQUEST: titleBeginning is blank",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
    ])
    @GetMapping("") @JsonView(Views.GetSeriesStartingWith::class)
    fun getSeriesStaringWith(

        @Parameter(description = "The beginning of the title.")
        @RequestParam
        titleBeginning: String

    ): ResponseEntity<List<Series>> {
        if (titleBeginning.isBlank()) return ResponseEntity.badRequest().build()
        val series = ss.getSeriesStartingWith(titleBeginning)
        return ResponseEntity.ok(series)
    }

    @Operation(
        summary = "Get series with id",
        description = "Get the series with the id provided as parameter.",
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "OK: Found series",
            content = [Content(
                schema = Schema(implementation = Series::class),
                mediaType = "application/json",
            )]
        ),
        ApiResponse(
            responseCode = "404",
            description = "NOT_FOUND: No series with id found",
            content = [Content(schema = Schema(implementation = Unit::class))]
        ),
    ])
    @GetMapping("/{id}") @JsonView(Views.GetSeries::class)
    fun getSeries(

        @Parameter(description = "The id of the series.")
        @PathVariable
        id: Long

    ): ResponseEntity<Series> {
        return try {
            ResponseEntity.ok(ss.getSeries(id))
        } catch (_: SeriesService.NoSeriesWithIdException) {
            ResponseEntity.notFound().build()
        }
    }
}