package com.mehdiflix.mehdiflix.domain

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.BillViews
import com.mehdiflix.mehdiflix.UserViews
import com.mehdiflix.mehdiflix.ViewViews
import com.mehdiflix.mehdiflix.Views
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
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

    @OneToMany(cascade = [CascadeType.ALL])
    var personalSpace: MutableSet<PersonalSpaceEntry> = mutableSetOf(),

    @OneToMany(cascade = [CascadeType.ALL]) @JsonView(UserViews.Bills::class)
    var bills: MutableList<Bill> = mutableListOf(Bill.empty(subscriptionType)),

    @Id @GeneratedValue @JsonView(UserViews.Id::class)
    var id: Long? = null,
) {

    @get:JsonView(UserViews.StartedSeries::class)
    val startedSeries: Set<Series> get() {
        return personalSpace.filter {
            it.mostAdvancedView != null
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

    override fun hashCode(): Int {
        return series.hashCode()
    }
}

enum class SubscriptionType(val fixedFee: BigDecimal) {
    STANDARD(BigDecimal(0.0)),
    PREMIUM(BigDecimal(20.0)),
}

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

@Entity
data class Bill(

    @JsonView(BillViews.Date::class)
    var date: LocalDate,

    @JsonView(BillViews.SubscriptionType::class)
    var subscriptionType: SubscriptionType,

    @OneToMany(cascade = [CascadeType.MERGE, CascadeType.PERSIST]) @JsonView(BillViews.Views::class)
    val views: MutableList<View> = mutableListOf(),

    @Id @GeneratedValue @JsonView(BillViews.Id::class)
    var id: Long? = null,
) {

    @get:JsonView(BillViews.Total::class)
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
