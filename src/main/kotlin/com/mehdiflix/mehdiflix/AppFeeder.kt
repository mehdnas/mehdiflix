package com.mehdiflix.mehdiflix

import com.mehdiflix.mehdiflix.domain.*
import com.mehdiflix.mehdiflix.repositories.SeriesRepository
import com.mehdiflix.mehdiflix.repositories.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.*

@Component
class AppFeeder(
    private val ur: UserRepository,
    private val sr: SeriesRepository
) : CommandLineRunner {

    @Throws(Exception::class)
    override fun run(vararg args: String) {
        feedSeries()
        feedUsers()

        println("Application feeded")
    }

    private fun feedSeries() {
        val s1 = Series(
            "Tawny Ranking",
            "Un Tawny owl Perdido en su Mundo",
            setOf("Creator One", "Creator Two"),
            setOf("Actor One", "Actor Two"),
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
            setOf("Creator One", "Creator Two"),
            setOf("Actor One", "Actor Two"),
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
            setOf("Creator One", "Creator Two"),
            setOf("Actor One", "Actor Two"),
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
//        val dateParser = SimpleDateFormat("dd-MM-yyyy")
//        var sample: Date? = null
//        try {
//            sample = dateParser.parse("00-01-2020")
//        } catch (e: ParseException) {
//            println("Crujo parseando fecha")
//            e.printStackTrace()
//        }
//
//        // Set<Viaje> viajes = vr.findByOrigenCiudadAndDestinoCiudad("Santander","Cadiz");
//        var viajes: Set<Viaje?> = vr.findByOrigenAndDestino("Santander", "Cadiz")
//
//        println("Viajes recuperados = " + viajes.size)
//
//        for (v in viajes) {
//            System.out.println("Viaje in " + v.getFecha())
//        }
//
//        viajes = vr.findByOrigen_CiudadAndFechaBeforeOrderByPrecio("Santander", sample)
//
//        println("================================")
//
//        println("Viajes recuperados = " + viajes.size)


        //		Usuario paco = ur.findByEmail("paco@carSharing.es");
//
//		System.out.println("Paco = " + paco.getNombre() + ":" + paco.getEmail());
//
//		Set<Usuario> usuarios = ur.findByFechaAltaAfter(sample);
//		for(Usuario u : usuarios) {
//			System.out.println("Usuario " + u.getNombre() + ":" + u.getEmail());
//		}
    }
}