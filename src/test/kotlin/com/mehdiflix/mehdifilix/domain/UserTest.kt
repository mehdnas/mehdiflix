package com.mehdiflix.mehdifilix.domain

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
                        1, 2
                    )
                )
            )
        )

    }

    @Test
    fun billsTest() {
        assert(users[0].bills.map { it.total.toDouble() } == listOf(0.5, 1.0))
    }

    @Test
    fun addSeriesToPersonalSpaceTest() {
        assert(users[0].addedSeries.size == 1)
        assert(users[0].pendingSeries.isEmpty())
        addSeriesToUser()
        assert(users[0].addedSeries.size == 2)
        assert(users[0].pendingSeries.size == 1)
        assert(users[0].pendingSeries[0].id == 1L)
    }

    @Test
    fun viewEpisodeTest() {
        assert(users[0].finishedSeries == emptyList<Series>())
        addSeriesToUser()
        assertThrows<User.SeriesNotAddedException> {
            users[0].viewEpisode(
                Series(2, "Serie3", "Description",
                    listOf("c1", "c2"), listOf("a1", "a2"),
                    SeriesType("Gold", BigDecimal(1.5)),
                    mutableListOf(
                        Season(4, 1, "Season1",
                            listOf(
                                Episode(8, 1, "E1", "BlaBla"),
                            )
                        ),
                    )
                ),
                1, 1
            )
        }
        assert(users[0].finishedSeries.isEmpty())
        assert(users[0].startedSeries.size == 1)
        users[0].viewEpisode(users[0].addedSeries[0], 1, 3)
        assert(users[0].finishedSeries.size == 1)
        assert(users[0].startedSeries.size == 0)
        users[0].viewEpisode(users[0].addedSeries[1], 1, 2)
        assert(users[0].finishedSeries.size == 1)
        assert(users[0].pendingSeries.isEmpty())
        assert(users[0].startedSeries.size == 1)
        users[0].viewEpisode(users[0].addedSeries[1], 2, 3)
        assert(users[0].finishedSeries.size == 2)
        assert(users[0].pendingSeries.isEmpty())
        assert(users[0].startedSeries.isEmpty())
    }

    private fun addSeriesToUser() {
        users[0].addSeriesToPersonalSpace( Series(1, "Serie2", "Description",
            listOf("c1", "c2"), listOf("a1", "a2"),
            SeriesType("Gold", BigDecimal(1.5)),
            mutableListOf(
                Season(2, 1, "Season1",
                    listOf(
                        Episode(3, 1, "E1", "BlaBla"),
                        Episode(4, 2, "E2", "BlaBla"),
                    )
                ),
                Season(3, 2, "Season1",
                    listOf(
                        Episode(6, 1, "E1", "BlaBla"),
                        Episode(7, 2, "E2", "BlaBla"),
                        Episode(8, 3, "E3", "BlaBla"),
                    )
                )
            )
        ))
    }
}