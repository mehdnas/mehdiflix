package com.mehdiflix.mehdiflix

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import io.swagger.v3.oas.annotations.servers.Servers
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "Mehdiflix API",
        version = "0.0",
        description = "API for Mehdiflix, a platform for on-demand streaming. The API allows for series retrieval " +
                "and management of personal space of users.",
    ),
    servers = [
        Server(
            url = "http://localhost:8080",
            description = "Local host link.",
        )
    ]
)
@SpringBootApplication
class MehdiflixApplication

fun main(args: Array<String>) {
    runApplication<MehdiflixApplication>(*args)
}
