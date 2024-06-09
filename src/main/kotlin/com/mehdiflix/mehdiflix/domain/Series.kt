package com.mehdiflix.mehdiflix.domain

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.*
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
data class Series (

    @JsonView(SeriesViews.Title::class)
    var title: String,

    @JsonView(SeriesViews.Description::class)
    var description: String,

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE]) @JsonView(SeriesViews.Creators::class)
    var creators: Set<Person>,

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE]) @JsonView(SeriesViews.Actors::class)
    var actors: Set<Person>,

    @JsonView(SeriesViews.SeriesType::class)
    var seriesType: SeriesType,

    @OneToMany(cascade = [CascadeType.ALL]) @JsonView(SeriesViews.Seasons::class)
    var seasons: MutableList<Season> = mutableListOf(),

    @Id @GeneratedValue @JsonView(SeriesViews.Id::class)
    var id: Long? = null,
) {

    @get:JsonView(SeriesViews.EpisodePrice::class)
    val episodePrice: BigDecimal get() {
        return seriesType.episodePrice
    }
}

@Entity
data class Person(

    @JsonView(PersonViews.Name::class)
    val firstName: String,

    @JsonView(PersonViews.Surname::class)
    val lastName: String,

    @JsonView(PersonViews.SecondSurname::class)
    val secondLastName: String? = null,

    @Id @GeneratedValue @JsonView(PersonViews.Id::class)
    var id: Long? = null,
) {
    override fun hashCode(): Int {
        return this.id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Person

        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (secondLastName != other.secondLastName) return false
        if (id != other.id) return false

        return true
    }

}

enum class SeriesType(val episodePrice: BigDecimal) {
    STANDARD(BigDecimal(0.5)),
    SILVER(BigDecimal(0.75)),
    GOLD(BigDecimal(1.5)),
}

@Entity
data class Season(

    @JsonView(SeasonViews.Number::class)
    var number: Int,

    @OneToMany(cascade = [CascadeType.ALL]) @JsonView(SeasonViews.Episode::class)
    var episodes: MutableList<Episode>,

    @Id @GeneratedValue @JsonView(SeasonViews.Id::class)
    var id: Long? = null,
)

@Entity
data class Episode(

    @JsonView(EpisodeViews.Number::class)
    var number: Int,

    @JsonView(EpisodeViews.Title::class)
    var title: String,

    @JsonView(EpisodeViews.Description::class)
    var description: String,

    @Id @GeneratedValue @JsonView(EpisodeViews.Id::class)
    var id: Long? = null,
)

