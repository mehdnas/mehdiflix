package com.mehdiflix.mehdiflix

import com.mehdiflix.mehdiflix.domain.*
import com.mehdiflix.mehdiflix.repositories.PersonRepository
import com.mehdiflix.mehdiflix.repositories.SeriesRepository
import com.mehdiflix.mehdiflix.repositories.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.*

@Component
class AppFeeder(
    private val ur: UserRepository,
    private val sr: SeriesRepository,
    private val pr: PersonRepository,
) : CommandLineRunner {

    @Throws(Exception::class)
    override fun run(vararg args: String) {
        feedSeries()
        feedUsers()
        testUserRepository()
        println("Application fed")
    }

    private fun feedSeries() {

        val c1 = pr.save(Person("Creator", "One"))
        val c2 = pr.save(Person("Creator", "Two", "Stwo"))
        val c3 = pr.save(Person("Creator", "Three", "Sthree"))

        val a1 = pr.save(Person("Actor", "One"))
        val a2 = pr.save(Person("Actor", "Two", "Stwo"))
        val a3 = pr.save(Person("Actor", "Three", "Sthree"))

        val s1 = Series(
            "Tawny Ranking",
            "Un Tawny Owl Perdido en su Mundo",
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
            "Jayal en su Natural Habitat",
            "Se sigue a Jayal para verlo en su natural habitat",
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
            "El Fichero de las Graficas",
            "Unas graficas que se tiran como ficheros.",
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
        sr.save(s1)
        sr.save(s2)
        sr.save(s3)
    }

    private fun feedUsers() {
        val u1 = User(
            "mehdi",
            "m1234567",
            "ES0000000001",
            SubscriptionType.STANDARD,
            mutableListOf(),
            mutableListOf(),
        )
        val u2 = User(
            "jayal",
            "j1234567",
            "ES0000000002",
            SubscriptionType.PREMIUM,
            mutableListOf(),
            mutableListOf(),
        )
        val u3 = User(
            "tawny",
            "t1234567",
            "ES0000000003",
            SubscriptionType.STANDARD,
            mutableListOf(),
            mutableListOf(),
        )
        ur.save(u1)
        ur.save(u2)
        ur.save(u3)
    }


    private fun testUserRepository() {
        val user = ur.findUserByUsername("mehdi").orElseThrow()
        assert(
            user.username == "mehdi" &&
            user.password == "m1234567" &&
            user.bankAccountIBAN == "ES0000000001" &&
            user.subscriptionType == SubscriptionType.STANDARD
        )

        assert(ur.existsByUsername("tawny"))
    }
}