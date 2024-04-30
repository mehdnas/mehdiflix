package com.mehdiflix.mehdiflix.services

import com.mehdiflix.mehdiflix.domain.Series
import com.mehdiflix.mehdiflix.domain.User
import com.mehdiflix.mehdiflix.repositories.SeriesRepository
import com.mehdiflix.mehdiflix.repositories.UserRepository
import com.mehdiflix.mehdiflix.services.SeriesService.NoSeriesWithIdException
import org.springframework.stereotype.Service

@Service
class UserService(val ur: UserRepository, val sr: SeriesRepository) {

    class NoUserWithIdException : RuntimeException()
    class NoUserWithUserNameException : RuntimeException()
    class UsernameAlreadyExistsException : RuntimeException()

    fun addUser(user: User): User {
        if (ur.existsByUsername(user.username))
            throw UsernameAlreadyExistsException()
        return ur.save(user)
    }

    fun getUser(username: String) = ur.findUserByUsername(username)
        .orElseThrow { NoUserWithUserNameException() }
    fun getUser(userId: Long) = ur.findById(userId).orElseThrow { NoUserWithIdException() }

    fun getAddedSeriesOfUser(userId: Long): List<Series> {
        val user = ur.findById(userId).orElseThrow { NoUserWithIdException() }
        return user.addedSeries
    }

    fun addSeriesToUser(userId: Long, seriesId: Long) {
        val series = sr.findById(seriesId).orElseThrow { NoSeriesWithIdException() }
        val user = ur.findById(userId).orElseThrow { NoUserWithIdException() }
        user.addSeries(series)
        ur.save(user)
    }

    fun addViewToUser(
        userId: Long, seriesId: Long,
        seasonNumber: Int, episodeNumber: Int
    ) {
        val series = sr.findById(seriesId).orElseThrow { NoSeriesWithIdException() }
        val user = ur.findById(userId).orElseThrow { NoUserWithIdException() }

        user.viewEpisode(series, seasonNumber, episodeNumber)
    }
}
