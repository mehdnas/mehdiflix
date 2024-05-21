package com.mehdiflix.mehdiflix.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.Views
import com.mehdiflix.mehdiflix.services.SeriesService
import com.mehdiflix.mehdiflix.domain.Series
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.*

@RestControllerAdvice
@RestController
// @Cors
@RequestMapping("/series")
class SeriesController(val ss: SeriesService) {

    @Operation(summary = "Get series with the title stating with the string")
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
        @RequestParam
        titleBeginning: String
    ): ResponseEntity<List<Series>> {
        if (titleBeginning.isBlank()) return ResponseEntity.badRequest().build()
        val series = ss.getSeriesStartingWith(titleBeginning)
        return ResponseEntity.ok(series)
    }

    @Operation(summary = "Get series with id")
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
    fun getSeries(@PathVariable id: Long): ResponseEntity<Series> {
        return try {
            ResponseEntity.ok(ss.getSeries(id))
        } catch (_: SeriesService.NoSeriesWithIdException) {
            ResponseEntity.notFound().build()
        }
    }
}