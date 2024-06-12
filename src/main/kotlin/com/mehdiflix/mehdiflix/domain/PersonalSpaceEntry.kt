package com.mehdiflix.mehdiflix.domain

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
data class PersonalSpaceEntry(

    @ManyToOne
    var series: Series,

    @OneToMany(mappedBy = "personalSpaceEntry", cascade = [CascadeType.PERSIST])
    var views: MutableList<View> = mutableListOf(),

    @OneToOne(cascade = [CascadeType.PERSIST])
    var mostAdvancedView: View? = null,

    @Id @GeneratedValue
    var id: Long? = null,
) {

    var isSeriesFinished: Boolean = false
        get() {
            if (!field) {
                val lastSeason = series.seasons.last()
                val lastEpisode = lastSeason.episodes.last()

                field = lastSeason.number == mostAdvancedView?.seasonNumber
                        && lastEpisode.number == mostAdvancedView?.episodeNumber
            }
            return field
        }

    fun addView(
        series: Series,
        seasonNumber: Int,
        episodeNumber: Int,
        timestamp: ZonedDateTime
    ): View {
        if (seasonNumber <= 0 || seasonNumber > series.seasons.size)
            throw SeasonNumberNotInSeriesException()
        if (episodeNumber <= 0 || episodeNumber > series.seasons[seasonNumber - 1].episodes.size)
            throw EpisodeNumberNotInSeasonException()

        val view = View(
            seasonNumber, episodeNumber,
            timestamp, series.seriesType.episodePrice,
            this,
        )
        views.add(view)

        val isSameSeason = seasonNumber == (mostAdvancedView?.seasonNumber ?: -1)
        val isMoreAdvancedSeason = seasonNumber > (mostAdvancedView?.seasonNumber ?: -1)
        val isMoreAdvancedEpisode = episodeNumber > (mostAdvancedView?.episodeNumber ?: -1)

        if (isMoreAdvancedSeason || (isSameSeason && isMoreAdvancedEpisode)) {
            mostAdvancedView = view
        }
        return view
    }

    override fun hashCode(): Int {
        return series.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonalSpaceEntry

        if (series != other.series) return false
        if (id != other.id) return false

        return true
    }
}