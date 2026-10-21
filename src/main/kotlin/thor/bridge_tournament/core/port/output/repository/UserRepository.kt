package thor.bridge_tournament.core.port.output.repository

import java.util.UUID
import thor.bridge_tournament.core.port.dto.UserDto

interface UserRepository {
    fun addUser(user: UserDto): UUID

    fun allPlayers(): List<UserDto>

    fun getById(uuid: UUID): UserDto

    fun findByUsername(username: String): List<UserDto>

    fun findBySurname(surname: String): List<UserDto>
}
