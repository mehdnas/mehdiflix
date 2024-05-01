package com.mehdiflix.mehdiflix.services

import com.mehdiflix.mehdiflix.domain.Series
import com.mehdiflix.mehdiflix.repositories.SeriesRepository
import org.springframework.stereotype.Service

@Service
class SeriesService(val sr: SeriesRepository) {

    class NoSeriesWithIdException : RuntimeException()

    fun addSeries(series: Series) = sr.save(series)

    fun getAllSeries(): MutableList<Series> = sr.findAll()

    fun getSeries(seriesId: Long): Series = sr.findById(seriesId)
        .orElseThrow { NoSeriesWithIdException() }
}