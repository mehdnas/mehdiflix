package com.mehdiflix.mehdiflix.services

import com.mehdiflix.mehdiflix.domain.Series
import com.mehdiflix.mehdiflix.repositories.SeriesRepository
import org.springframework.stereotype.Service

@Service
class SeriesService(val sr: SeriesRepository) {

    class NoSeriesWithIdException : RuntimeException()

    fun addSeries(series: Series): Series {
        return sr.save(series)
    }

    fun getSeriesStartingWith(titleBeginning: String): List<Series> {
        return sr.findSeriesByTitleStartingWithIgnoreCase(titleBeginning)
    }
}