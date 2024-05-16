package com.mehdiflix.mehdiflix.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.Views
import com.mehdiflix.mehdiflix.services.SeriesService
import com.mehdiflix.mehdiflix.domain.Series
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestControllerAdvice
@RestController
@RequestMapping("/series")
class SeriesController(val ss: SeriesService) {

    @Operation(summary = "Get series with the title stating with the string")
    @GetMapping("/{titleBeginning}") @JsonView(Views.GetSeries::class)
    fun getSeriesStaringWith(
        @PathVariable
        titleBeginning: String
    ): ResponseEntity<List<Series>> {
        val series = ss.getSeriesStartingWith(titleBeginning)
        return ResponseEntity.ok(series)
    }

    @Operation(summary = "Create a new series")
    @PostMapping(consumes = ["application/json"]) @JsonView(Views.GetSeries::class)
    fun createSeries(
        @RequestBody @JsonView(Views.PostSeries::class)
        series: Series
    ): ResponseEntity<Series> {
        val added = ss.addSeries(series)
        return ResponseEntity.ok(added)
    }
}