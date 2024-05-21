package com.mehdiflix.mehdiflix.services

import com.mehdiflix.mehdiflix.domain.Person
import com.mehdiflix.mehdiflix.domain.Series
import com.mehdiflix.mehdiflix.repositories.SeriesRepository
import org.springframework.stereotype.Service

@Service
class SeriesService(val sr: SeriesRepository) {

    class NoSeriesWithIdException : RuntimeException()

    fun getSeriesStartingWith(titleBeginning: String): List<Series> {
        return sr.findSeriesByTitleStartingWithIgnoreCase(titleBeginning)
    }

    fun getSeries(id: Long): Series {
        return sr.findById(id).orElseThrow { NoSeriesWithIdException() }
    }
}