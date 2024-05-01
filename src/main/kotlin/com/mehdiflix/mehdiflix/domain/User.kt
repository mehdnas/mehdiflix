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

    @ManyToMany
    var addedSeries: MutableList<Series> = mutableListOf(),

    @OneToMany(cascade = [CascadeType.ALL])
    var views: MutableList<View> = mutableListOf(),

    @Id @GeneratedValue @JsonView(Views.WithId::class)
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
    @JsonView(Views.Public::class)
    var seasonNumber: Int,
    @JsonView(Views.Public::class)
    var episodeNumber: Int,
    @ManyToOne @JsonView(Views.Public::class)
    var series: Series,
    @JsonView(Views.Public::class)
    var timestamp: ZonedDateTime,
    @JsonView(Views.Public::class)
    var subscriptionType: SubscriptionType,
    @JsonView(Views.Public::class)
    var cost: BigDecimal,
    @Id @GeneratedValue @JsonView(Views.WithId::class)
    var id: Long? = null,
) {
    val isOfLastEpisode: Boolean get() {
        val lastSeasonNumber = series.seasons.last().number
        val lastEpisodeNumber = series.seasons.last().episodes.last().number
        return seasonNumber == lastSeasonNumber && episodeNumber == lastEpisodeNumber
    }
}

data class Bill(
    @JsonView(Views.Public::class)
    val date: LocalDate,
    @JsonView(Views.Public::class)
    val views: List<View>,
    @JsonView(Views.Public::class)
    val subscriptionType: SubscriptionType,
) {
    @get:JsonView(Views.Public::class)
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
