package com.mehdiflix.mehdiflix.domain

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
data class Series (
    var title: String,
    var description: String,
    var creators: Set<String>,
    var actors: Set<String>,
    var seriesType: SeriesType,
    @OneToMany(cascade = [CascadeType.ALL])
    var seasons: MutableList<Season>,
    @Id @GeneratedValue
    var id: Long? = null,
)

enum class SeriesType(val episodePrice: BigDecimal) {
    STANDARD(BigDecimal(0.5)),
    SILVER(BigDecimal(0.75)),
    GOLD(BigDecimal(1.5)),
}

@Entity
data class Season(
    var number: Int,
    var description: String,
    @OneToMany(cascade = [CascadeType.ALL])
    var episodes: MutableList<Episode>,
    @Id @GeneratedValue
    var id: Long? = null,
)

@Entity
data class Episode(
    var number: Int,
    var title: String,
    var description: String,
    @Id @GeneratedValue
    var id: Long? = null,
)

