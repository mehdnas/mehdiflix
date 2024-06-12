package com.mehdiflix.mehdiflix.domain

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.BillViews
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

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
    val total: BigDecimal
        get() {
        return if (subscriptionType == SubscriptionType.PREMIUM) {
            subscriptionType.fixedFee
        } else {
            views.sumOf { it.cost }
        }
    }

    companion object {
        fun empty(subscriptionType: SubscriptionType, timestamp: ZonedDateTime): Bill {
            return Bill(
                LocalDate.of(timestamp.year, timestamp.month, 1), subscriptionType
            )
        }
    }
}