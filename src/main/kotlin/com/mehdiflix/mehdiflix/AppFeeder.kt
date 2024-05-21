package com.mehdiflix.mehdiflix

import com.mehdiflix.mehdiflix.domain.*
import com.mehdiflix.mehdiflix.repositories.SeriesRepository
import com.mehdiflix.mehdiflix.repositories.UserRepository
import com.mehdiflix.mehdiflix.services.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class AppFeeder(
    private val ur: UserRepository,
    private val sr: SeriesRepository,
    private val us: UserService,
) : CommandLineRunner {

    @Throws(Exception::class)
    @Transactional
    override fun run(vararg args: String) {
        feedSeries()
        feedUsers()
        addViews()
        println("Application fed")
    }

    private fun addViews() {
        for (sId in 1..3) {
            us.addSeriesToUser(1, sId.toLong())
        }

        us.addViewToUser(1, 2, 1, 1)
        us.addViewToUser(1, 3, 2, 3)
    }

    private fun feedSeries() {

        val c1 = Person("Creator", "One")
        val c2 = Person("Creator", "Two", "Stwo")
        val c3 = Person("Creator", "Three", "Sthree")

        val a1 = Person("Actor", "One")
        val a2 = Person("Actor", "Two", "Stwo")
        val a3 = Person("Actor", "Three", "Sthree")

        val s1 = Series(
            "The Big Bang Theory",
            "Desc of The Big Bang Theory",
            setOf(c1, c2),
            setOf(a1, a2),
            SeriesType.STANDARD,
            mutableListOf(
                Season(1, mutableListOf(
                    Episode(1, "E1", "Desc E1"),
                    Episode(2, "E2", "Desc E2"),
                )),
            ),
        )
        val s2 = Series(
            "The Soprano",
            "Desc of The Soprano",
            setOf(c3),
            setOf(a1),
            SeriesType.SILVER,
            mutableListOf(
                Season(1, mutableListOf(
                    Episode(1, "E1", "Desc E1"),
                    Episode(2, "E2", "Desc E2"),
                    Episode(3, "E3", "Desc E3"),
                    Episode(4, "E4", "Desc E4"),
                )),
            ),
        )
        val s3 = Series(
            "The Walking Dead",
            "Desc of The Walking Dead",
            setOf(c1, c2, c3),
            setOf(a1, a2, a3),
            SeriesType.GOLD,
            mutableListOf(
                Season(1, mutableListOf(
                    Episode(1, "E1", "Desc E1"),
                    Episode(2, "E2", "Desc E2"),
                    Episode(3, "E3", "Desc E3"),
                )),
                Season(2, mutableListOf(
                    Episode(1, "E1", "Desc E1"),
                    Episode(2, "E2", "Desc E2"),
                    Episode(3, "E3", "Desc E3"),
                ))
            ),
        )
        val s4 = Series(
            "Spongebob Square Pants",
            "Desc of The Walking Dead",
            setOf(c1, c2, c3),
            setOf(a1, a2, a3),
            SeriesType.GOLD,
            mutableListOf(
                Season(1, mutableListOf(
                    Episode(1, "E1", "Desc E1"),
                    Episode(2, "E2", "Desc E2"),
                    Episode(3, "E3", "Desc E3"),
                )),
                Season(3, mutableListOf(
                    Episode(1, "E1", "Desc E1"),
                    Episode(2, "E2", "Desc E2"),
                    Episode(3, "E3", "Desc E3"),
                    Episode(4, "E1", "Desc E4"),
                    Episode(5, "E2", "Desc E5"),
                    Episode(6, "E3", "Desc E6"),
                )),
            ),
        )
        sr.save(s1)
        sr.save(s2)
        sr.save(s3)
        sr.save(s4)
    }

    private fun feedUsers() {
        val u1 = User(
            "pepe",
            "pw1",
            "ES01",
            SubscriptionType.STANDARD,
        )
        val u2 = User(
            "juan",
            "pw2",
            "ES02",
            SubscriptionType.PREMIUM,
        )
        val u3 = User(
            "ana",
            "pw3",
            "ES03",
            SubscriptionType.STANDARD,
        )
        ur.save(u1)
        ur.save(u2)
        ur.save(u3)
    }
}