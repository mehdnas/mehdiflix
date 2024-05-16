package com.mehdiflix.mehdiflix.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.Views
import com.mehdiflix.mehdiflix.services.SeriesService
import com.mehdiflix.mehdiflix.domain.Series
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/series")
class SeriesController(val ss: SeriesService) {

    @PostMapping("", consumes = ["application/json"]) @JsonView(Views.Write::class)
    fun createSeries(@RequestBody series: Series) = ss.addSeries(series)

    @GetMapping("") @JsonView(Views.Read::class)
    fun getAllSeries() = ss.getAllSeries()
}