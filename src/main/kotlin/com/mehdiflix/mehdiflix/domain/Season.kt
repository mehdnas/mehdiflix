package com.mehdiflix.mehdiflix.domain

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.SeasonViews
import jakarta.persistence.*

@Entity
data class Season(

    @JsonView(SeasonViews.Number::class)
    var number: Int,

    @OneToMany(cascade = [CascadeType.ALL]) @JsonView(SeasonViews.Episode::class)
    var episodes: MutableList<Episode>,

    @Id @GeneratedValue @JsonView(SeasonViews.Id::class)
    var id: Long? = null,
)