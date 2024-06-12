package com.mehdiflix.mehdiflix.domain

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.ViewViews
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.math.BigDecimal
import java.time.ZonedDateTime

@Entity
data class View(

    @JsonView(ViewViews.SeasonNumber::class)
    var seasonNumber: Int,

    @JsonView(ViewViews.EpisodeNumber::class)
    var episodeNumber: Int,

    @JsonView(ViewViews.TimeStamp::class)
    var timestamp: ZonedDateTime,

    @JsonView(ViewViews.Cost::class)
    var cost: BigDecimal,

    @ManyToOne
    var personalSpaceEntry: PersonalSpaceEntry,

    @Id @GeneratedValue @JsonView(ViewViews.Id::class)
    var id: Long? = null,
) {

    @get:JsonView(ViewViews.Series::class)
    val series: Series get() = personalSpaceEntry.series

    override fun toString(): String {
        return "View(seasonNumber=$seasonNumber, episodeNumber=$episodeNumber, timestamp=$timestamp, cost=$cost, series=${personalSpaceEntry.series} id=$id)"
    }
}