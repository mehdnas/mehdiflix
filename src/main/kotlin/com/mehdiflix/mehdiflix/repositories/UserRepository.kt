package com.mehdiflix.mehdiflix.repositories

import com.mehdiflix.mehdiflix.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findUserByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
}

