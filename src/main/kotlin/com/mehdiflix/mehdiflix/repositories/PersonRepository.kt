package com.mehdiflix.mehdiflix.repositories

import com.mehdiflix.mehdiflix.domain.Person
import org.springframework.data.jpa.repository.JpaRepository

interface PersonRepository  : JpaRepository<Person, Long> {
    fun findByNameAndSurnameAndSecondSurname(name: String, surname: String, secondSurname: String?): Person?
}