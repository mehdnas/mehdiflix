package com.mehdiflix.mehdiflix.domain

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.EpisodeViews
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

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