package com.mehdiflix.mehdifilix.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.math.BigDecimal

@Entity
class Series(
    @Id @GeneratedValue
    var id: Long,
    @Column(unique = true)
    var title: String,
    var description: String,
    var creators: List<String>,
    var actors: List<String>,
    @ManyToOne
    var seriesType: SeriesType,
    @OneToMany
    var seasons: MutableList<Season>
)

@Entity
class SeriesType(
    @Id
    var name: String,
    var episodePrice: BigDecimal,
)

@Entity
class Season(
    @Id @GeneratedValue
    var id: Long,
    var number: Int,
    var description: String,
    @OneToMany
    var episodes: List<Episode>,
)

@Entity
class Episode(
    @Id @GeneratedValue
    var id: Long,
    var number: Int,
    var title: String,
    var description: String,
)

