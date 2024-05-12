package com.mehdiflix.mehdiflix

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@SpringBootApplication
class MehdiflixApplication

fun main(args: Array<String>) {
    runApplication<MehdiflixApplication>(*args)
}
