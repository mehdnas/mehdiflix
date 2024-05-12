package com.mehdiflix.mehdiflix.domain

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.Views
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

@Entity
@Table(name = "User_") // So it does not conflict with the reserved name of H2 database (USER)
data class User(

    @Column(unique = true) @JsonView(Views.Public::class)
    var username: String,

    @JsonView(Views.Private::class)
    var password: String,

    @JsonView(Views.Private::class)
    var bankAccountIBAN: String,

    @JsonView(Views.Public::class)
    var subscriptionType: SubscriptionType,

    @OneToMany(cascade = [CascadeType.ALL])
    var personalSpace: MutableSet<PersonalSpaceEntry> = mutableSetOf(),

    @OneToMany(cascade = [CascadeType.ALL])
    var bills: MutableSet<Bill> = mutableSetOf(Bill.empty(subscriptionType)),

    @Id @GeneratedValue @JsonView(Views.WithId::class)
    var id: Long? = null,
) {

    val startedSeries: Set<Series> get() {
        return personalSpace.filter {
            it.views.isNotEmpty()
        }.map { it.series }.toSet()
    }

    val pendingSeries: Set<Series> get() {
        return personalSpace.filter {
            it.views.isEmpty()
        }.map { it.series }.toSet()
    }

    val finishedSeries: Set<Series> get() {
        return personalSpace.filter {
            it.isSeriesFinished
        }.map { it.series }.toSet()
    }

    val addedSeries: Set<Series> get() {
        return personalSpace.map { it.series }.toSet()
    }

    fun addSeries(series: Series) {
        personalSpace.add(PersonalSpaceEntry(series))
    }


    fun viewEpisode(series: Series, seasonNumber: Int, episodeNumber: Int) {
        if (series !in addedSeries) throw SeriesNotAddedException()
        val personalSpaceEntry = personalSpace.first { it.series == series }
        val view = personalSpaceEntry.addView(series, seasonNumber, episodeNumber)
        // This might be costly computationally
        bills.maxBy { it.date }.views.add(view)
    }
}

class SeriesNotAddedException: RuntimeException()
class SeasonNumberNotInSeriesException: RuntimeException()
class EpisodeNumberNotInSeasonException: RuntimeException()

@Entity
data class PersonalSpaceEntry(
    @ManyToOne
    var series: Series,
    @OneToMany(mappedBy = "personalSpaceEntry", cascade = [CascadeType.PERSIST])
    var views: MutableSet<View> = mutableSetOf(),
    @OneToOne
    var mostAdvancedView: View? = null,
    @Id @GeneratedValue @JsonView(Views.WithId::class)
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

    fun addView(series: Series, seasonNumber: Int, episodeNumber: Int): View {
        if (seasonNumber <= 0 || seasonNumber > series.seasons.size)
            throw SeasonNumberNotInSeriesException()
        if (episodeNumber <= 0 || episodeNumber > series.seasons[seasonNumber - 1].episodes.size)
            throw EpisodeNumberNotInSeasonException()

        val view = View(
            seasonNumber, episodeNumber,
            ZonedDateTime.now(), series.seriesType.episodePrice,
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
}

enum class SubscriptionType(val fixedFee: BigDecimal) {
    STANDARD(BigDecimal(0.0)),
    PREMIUM(BigDecimal(20.0)),
}

@Entity
data class View(
    @JsonView(Views.Public::class)
    var seasonNumber: Int,
    @JsonView(Views.Public::class)
    var episodeNumber: Int,
    @JsonView(Views.Public::class)
    var timestamp: ZonedDateTime,
    @JsonView(Views.Public::class)
    var cost: BigDecimal,
    @ManyToOne
    var personalSpaceEntry: PersonalSpaceEntry,
    @Id @GeneratedValue @JsonView(Views.WithId::class)
    var id: Long? = null,
)

@Entity
data class Bill(
    @JsonView(Views.Public::class)
    var date: LocalDate,

    @JsonView(Views.Public::class)
    var subscriptionType: SubscriptionType,

    @OneToMany @JsonView(Views.Public::class)
    val views: MutableSet<View> = mutableSetOf(),

    @Id @GeneratedValue @JsonView(Views.WithId::class)
    var id: Long? = null,
) {
    @get:JsonView(Views.Public::class)
    val total: BigDecimal get() {
        return if (subscriptionType == SubscriptionType.PREMIUM) {
            subscriptionType.fixedFee
        } else {
            views.sumOf { it.cost }
        }
    }

    companion object {
        fun empty(subscriptionType: SubscriptionType): Bill {
            val today = LocalDate.now()
            return Bill(
                LocalDate.of(today.year, today.month, 1), subscriptionType
            )
        }
    }
}
