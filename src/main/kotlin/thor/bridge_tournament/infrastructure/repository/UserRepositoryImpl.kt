package thor.bridge_tournament.infrastructure.repository

import java.util.UUID
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import thor.bridge_tournament.core.exception.UserNotFoundException
import thor.bridge_tournament.core.port.dto.UserDto
import thor.bridge_tournament.core.port.output.repository.UserRepository
import thor.bridge_tournament.infrastructure.jpa.repository.UserJpaRepository
import thor.bridge_tournament.infrastructure.mapping.UserMapper

@Repository
class UserRepositoryImpl(
    private val repository: UserJpaRepository,
    private val mapper: UserMapper
) : UserRepository {
    @Transactional
    override fun addUser(user: UserDto): UUID {
        return requireNotNull(repository.save(mapper.toJpa(user)).id)
    }

    override fun allPlayers(): List<UserDto> {
        return repository.findAll().map(mapper::toDto)
    }

    override fun getById(uuid: UUID): UserDto {
        return mapper.toDto(repository.findById(uuid).orElseThrow { UserNotFoundException(uuid) })
    }

    override fun findByUsername(username: String): List<UserDto> {
        return repository.findByUsername(username).map(mapper::toDto)
    }

    override fun findBySurname(surname: String): List<UserDto> {
        return repository.findBySurname(surname).map(mapper::toDto)
    }
}
