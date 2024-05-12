package com.mehdiflix.mehdiflix.domain

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.Views
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
data class Series (

    @JsonView(Views.Public::class)
    var title: String,

    @JsonView(Views.Public::class)
    var description: String,

    @ManyToMany @JsonView(Views.Public::class)
    var creators: Set<Person>,

    @ManyToMany @JsonView(Views.Public::class)
    var actors: Set<Person>,

    @JsonView(Views.Public::class)
    var seriesType: SeriesType,

    @OneToMany(cascade = [CascadeType.ALL])
    var seasons: MutableList<Season> = mutableListOf(),

    @Id @GeneratedValue @JsonView(Views.WithId::class)
    var id: Long? = null,
) {
    @get:JsonView(Views.Public::class)
    val episodePrice: BigDecimal get() {
        return seriesType.episodePrice
    }
}

@Entity
@Table(uniqueConstraints = [
    UniqueConstraint(columnNames = ["name", "surname", "secondSurname"])
])
data class Person(
    @JsonView(Views.Public::class)
    val name: String,
    @JsonView(Views.Public::class)
    val surname: String,
    @JsonView(Views.Public::class)
    val secondSurname: String? = null,
    @Id @GeneratedValue @JsonView(Views.WithId::class)
    var id: Long? = null,
)

enum class SeriesType(val episodePrice: BigDecimal) {
    STANDARD(BigDecimal(0.5)),
    SILVER(BigDecimal(0.75)),
    GOLD(BigDecimal(1.5)),
}

@Entity
data class Season(

    @JsonView(Views.Public::class)
    var number: Int,

    @JsonView(Views.Public::class)
    @OneToMany(cascade = [CascadeType.ALL])
    var episodes: MutableList<Episode>,

    @Id @GeneratedValue @JsonView(Views.WithId::class)
    var id: Long? = null,
)

@Entity
data class Episode(

    @JsonView(Views.Public::class)
    var number: Int,

    @JsonView(Views.Public::class)
    var title: String,

    @JsonView(Views.Public::class)
    var description: String,

    @Id @GeneratedValue @JsonView(Views.WithId::class)
    var id: Long? = null,
)

