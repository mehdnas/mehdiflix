package com.mehdiflix.mehdiflix.repositories

import com.mehdiflix.mehdiflix.domain.Series
import org.springframework.data.jpa.repository.JpaRepository

interface SeriesRepository : JpaRepository<Series, Long>