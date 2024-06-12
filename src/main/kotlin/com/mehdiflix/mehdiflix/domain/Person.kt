package com.mehdiflix.mehdiflix.domain

import com.fasterxml.jackson.annotation.JsonView
import com.mehdiflix.mehdiflix.PersonViews
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

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