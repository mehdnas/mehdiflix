package com.mehdiflix.mehdiflix.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.Views
import com.mehdiflix.mehdiflix.services.SeriesService
import com.mehdiflix.mehdiflix.domain.Series
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/series")
class SeriesController(val ss: SeriesService) {

    @PutMapping("", consumes = ["application/json"])
    @JsonView(Views.Public::class, Views.Private::class)
    fun createSeries(@RequestBody series: Series) = ss.addSeries(series)

    @GetMapping("") @JsonView(Views.WithId::class)
    fun getAllSeries() = ss.getAllSeries()
}