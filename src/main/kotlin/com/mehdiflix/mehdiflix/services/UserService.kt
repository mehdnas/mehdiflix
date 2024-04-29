package com.mehdiflix.mehdiflix.services

import com.mehdiflix.mehdiflix.domain.User
import com.mehdiflix.mehdiflix.domain.View
import com.mehdiflix.mehdiflix.repositories.UserRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.ZonedDateTime

@Service
class UserService(val ur: UserRepository) {

    fun addUser(user: User) = ur.save(user)

    fun getAllUsers(): List<User> = ur.findAll()

    fun getUser(username: String) = ur.findUserByUsername(username)

    fun addViewToUser(userId: Long, view: View): View {
        var user = ur.findById(userId).orElseThrow()
        view.timestamp = ZonedDateTime.now()
        view.subscriptionType = user.subscriptionType
        view.cost = BigDecimal(0.5)
        user.views.add(view)
        return ur.save(user).views.last()
    }
}
