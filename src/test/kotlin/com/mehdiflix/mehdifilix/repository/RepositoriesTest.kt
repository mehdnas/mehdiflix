package com.mehdiflix.mehdifilix.repository

import com.mehdiflix.mehdifilix.domain.*
import com.mehdiflix.mehdifilix.repositories.EpisodeRepository
import com.mehdiflix.mehdifilix.repositories.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class RepositoriesTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val episodeRepository: EpisodeRepository,
) {

    @Test
    fun `When findByUserName then return User`() {
        val episode = Episode(1, "Title", "Desc")
        entityManager.persist(episode)
        entityManager.flush()

        val episodes = episodeRepository.findAll()

        assert(episodes.size == 1)
    }

}