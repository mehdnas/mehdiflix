package com.mehdiflix.mehdifilix.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

@Entity
class User (
    @Id @GeneratedValue
    var id: Long,
    @Column(unique = true)
    var userName: String,
    var password: String,
    var bankAccountIBAN: String,
    var subscriptionType: SubscriptionType,
    @OneToMany
    var addedSeries: MutableList<Series>,
    @OneToMany
    var views: MutableList<View>,
) {
    val startedSeries: List<Series> get() {
        return views.filter { !it.isOfLastEpisode }.map { it.series }.distinct()
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
        }.map { Bill(it.key, it.value) }
    }
}

enum class SubscriptionType {
    STANDARD,
    FIXED_FEE,
}

@Entity
class View(
    @Id @GeneratedValue
    var id: Long,
    var timestamp: ZonedDateTime,
    var subscriptionType: SubscriptionType,
    var cost: BigDecimal,
    var series: Series,
    var seasonNumber: Int,
    var episodeNumber: Int,
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
) {
    val total: BigDecimal get() {
        return views.sumOf { it.cost }
    }
}