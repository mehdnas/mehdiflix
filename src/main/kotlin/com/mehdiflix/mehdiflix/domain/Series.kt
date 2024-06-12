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

enum class SeriesType(val episodePrice: BigDecimal) {
    STANDARD(BigDecimal(0.5)),
    SILVER(BigDecimal(0.75)),
    GOLD(BigDecimal(1.5)),
}

