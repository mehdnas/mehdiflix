package com.mehdiflix.mehdiflix.domain

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.Views.NewUser
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

@Entity
@Table(name = "User_") // So it does not conflict with the reserved name of H2 database (USER)
data class User(

    @Column(unique = true) @JsonView(NewUser::class)
    var username: String,

    @JsonView(NewUser::class)
    var password: String,

    @JsonView(NewUser::class)
    var bankAccountIBAN: String,

    @JsonView(NewUser::class)
    var subscriptionType: SubscriptionType,

    @ManyToMany
    var addedSeries: MutableList<Series>,

    @OneToMany(cascade = [CascadeType.ALL])
    var views: MutableList<View>,

    @Id @GeneratedValue
    var id: Long? = null,
) {
    val startedSeries: List<Series> get() {
        val finished = finishedSeries.toSet()
        return views.map { it.series }.distinct().filter { it !in finished }
    }

    val pendingSeries: List<Series> get() {
        val viewed = views.map { it.series }.toSet()
        return addedSeries.filter { it !in viewed }
    }

    val finishedSeries: List<Series> get() {
        return views.filter { it.isOfLastEpisode }.map { it.series }.distinct()
    }

    val bills: List<Bill> get() {
        return views.groupBy {
            LocalDate.of(it.timestamp.year, it.timestamp.month.value, 1)
        }.map {
            Bill(it.key, it.value, subscriptionType)
        }.ifEmpty { listOf(Bill.empty(subscriptionType)) }
    }

    fun addSeries(series: Series) {
        addedSeries.add(series)
    }

    class SeriesNotAddedException: RuntimeException()
    class SeasonNumberNotInSeriesException: RuntimeException()
    class EpisodeNumberNotInSeasonException: RuntimeException()

    fun viewEpisode(series: Series, seasonNumber: Int, episodeNumber: Int) {
        if (series !in addedSeries) throw SeriesNotAddedException()
        if (seasonNumber <= 0 || seasonNumber > series.seasons.size)
            throw SeasonNumberNotInSeriesException()
        if (episodeNumber <= 0 || episodeNumber > series.seasons[seasonNumber - 1].episodes.size)
            throw EpisodeNumberNotInSeasonException()

        views.add(View(
            seasonNumber, episodeNumber, series,
            ZonedDateTime.now(), subscriptionType, series.seriesType.episodePrice,
        ))
    }
}

enum class SubscriptionType(val fixedFee: BigDecimal) {
    STANDARD(BigDecimal(0.0)),
    PREMIUM(BigDecimal(20.0)),
}

@Entity
data class View(
    var seasonNumber: Int,
    var episodeNumber: Int,
    @ManyToOne
    var series: Series,
    var timestamp: ZonedDateTime,
    var subscriptionType: SubscriptionType,
    var cost: BigDecimal,
    @Id @GeneratedValue
    var id: Long? = null,
) {
    val isOfLastEpisode: Boolean get() {
        val lastSeasonNumber = series.seasons.last().number
        val lastEpisodeNumber = series.seasons.last().episodes.last().number
        return seasonNumber == lastSeasonNumber && episodeNumber == lastEpisodeNumber
    }
}

data class Bill(
    val date: LocalDate,
    val views: List<View>,
    val subscriptionType: SubscriptionType,
) {
    val total: BigDecimal get() {
        val fixedFee = views.groupBy {
            it.subscriptionType
        }.map { it.key }.ifEmpty { listOf(this.subscriptionType) }.sumOf { it.fixedFee }

        return fixedFee + views.filter {
            it.subscriptionType == SubscriptionType.STANDARD
        }.sumOf { it.cost }
    }

    companion object {
        fun empty(subscriptionType: SubscriptionType): Bill {
            val today = LocalDate.now()
            return Bill(
                LocalDate.of(today.year, today.month, 1),
                emptyList(), subscriptionType
            )
        }
    }
}
