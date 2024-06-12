package com.mehdiflix.mehdiflix.domain

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.UserViews
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.ZonedDateTime

@Entity
@Table(name = "User_") // So it does not conflict with the reserved name of H2 database (USER)
data class User(

    @Column(unique = true) @JsonView(UserViews.Username::class)
    var username: String,

    @JsonView(UserViews.Password::class)
    var password: String,

    @JsonView(UserViews.BankAccountIBAN::class)
    var bankAccountIBAN: String,

    @JsonView(UserViews.SubscriptionType::class)
    var subscriptionType: SubscriptionType,

    @OneToMany(cascade = [CascadeType.ALL]) @JsonView(UserViews.PersonalSpace::class)
    var personalSpace: MutableSet<PersonalSpaceEntry> = mutableSetOf(),

    @OneToMany(cascade = [CascadeType.ALL]) @JsonView(UserViews.Bills::class)
    var bills: MutableSet<Bill> = mutableSetOf(Bill.empty(subscriptionType, ZonedDateTime.now())),

    @Id @GeneratedValue @JsonView(UserViews.Id::class)
    var id: Long? = null,
) {

    @get:JsonView(UserViews.StartedSeries::class)
    val startedSeries: Set<Series> get() {
        return personalSpace.filter {
            it.mostAdvancedView != null && !it.isSeriesFinished
        }.map { it.series }.toSet()
    }

    @get:JsonView(UserViews.PendingSeries::class)
    val pendingSeries: Set<Series> get() {
        return personalSpace.filter {
            it.mostAdvancedView == null
        }.map { it.series }.toSet()
    }

    @get:JsonView(UserViews.FinishedSeries::class)
    val finishedSeries: Set<Series> get() {
        return personalSpace.filter {
            it.isSeriesFinished
        }.map { it.series }.toSet()
    }

    private val addedSeries: Set<Series> get() {
        return personalSpace.map { it.series }.toSet()
    }

    fun addSeries(series: Series) {
        personalSpace.add(PersonalSpaceEntry(series))
    }


    fun viewEpisode(
        series: Series,
        seasonNumber: Int,
        episodeNumber: Int,
        timestamp: ZonedDateTime,
    ) {
        if (series !in addedSeries) throw SeriesNotAddedException()
        val personalSpaceEntry = personalSpace.first { it.series == series }
        val view = personalSpaceEntry.addView(
            series, seasonNumber, episodeNumber, timestamp
        )
        val bill = bills.find {
            it.date.year == timestamp.year && it.date.month == timestamp.month
        }?: Bill.empty(subscriptionType, timestamp)

        bill.views.add(view)
    }
}

class SeriesNotAddedException: RuntimeException()
class SeasonNumberNotInSeriesException: RuntimeException()
class EpisodeNumberNotInSeasonException: RuntimeException()

enum class SubscriptionType(val fixedFee: BigDecimal) {
    STANDARD(BigDecimal(0.0)),
    PREMIUM(BigDecimal(20.0)),
}

