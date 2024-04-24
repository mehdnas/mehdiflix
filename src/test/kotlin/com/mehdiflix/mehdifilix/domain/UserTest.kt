package com.mehdiflix.mehdifilix.domain

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime

class UserTest {

    private var users = emptyList<User>()

    @BeforeEach
     fun init() {

         val series = mutableListOf(
             Series(0, "Serie1", "Description",
                 listOf("c1", "c2"), listOf("a1", "a2"),
                 SeriesType("Standard", BigDecimal(0.5)),
                 mutableListOf(
                     Season(0, 1, "Season1",
                         listOf(
                             Episode(0, 1, "E1", "BlaBla"),
                             Episode(1, 2, "E2", "BlaBla"),
                             Episode(2, 3, "E3", "BlaBla"),
                         )
                     )
                 )
             )
         )

        users = mutableListOf(
            User( 0, "username", "password", "ES103410293", SubscriptionType.STANDARD,
                series,
                mutableListOf(
                    View(
                        0,
                        ZonedDateTime.of(
                            2024,
                            1,
                            1,
                            9,
                            12,
                            23,
                            0,
                            ZoneId.systemDefault()
                        ),
                        SubscriptionType.STANDARD,
                        BigDecimal(0.5),
                        series[0],
                        1, 1
                    ),
                    View(
                        0,
                        ZonedDateTime.of(
                            2024,
                            2,
                            14,
                            9,
                            12,
                            23,
                            0,
                            ZoneId.systemDefault()
                        ),
                        SubscriptionType.STANDARD,
                        BigDecimal(0.5),
                        series[0],
                        1, 1
                    ),
                    View(
                        0,
                        ZonedDateTime.of(
                            2024,
                            2,
                            20,
                            9,
                            12,
                            23,
                            0,
                            ZoneId.systemDefault()
                        ),
                        SubscriptionType.STANDARD,
                        BigDecimal(0.5),
                        series[0],
                        1, 1
                    )
                )
            )
        )

    }

    @Test
    fun test() {
        assert(users[0].bills.map { it.total.toDouble() } == listOf(0.5, 1.0))
    }
}