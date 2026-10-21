package thor.bridge_tournament.infrastructure.jpa.repository

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import thor.bridge_tournament.infrastructure.jpa.UserEntity

interface UserJpaRepository : JpaRepository<UserEntity, UUID> {
    fun findByUsername(username: String): List<UserEntity>

    fun findBySurname(surname: String): List<UserEntity>
}
