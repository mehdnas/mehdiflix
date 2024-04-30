package com.mehdiflix.mehdiflix.repositories

import com.mehdiflix.mehdiflix.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    fun findUserByUsername(username: String): Optional<User>
    fun existsByUsername(username: String): Boolean
}

