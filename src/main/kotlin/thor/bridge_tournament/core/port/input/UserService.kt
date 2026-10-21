package thor.bridge_tournament.core.port.input

import java.util.UUID
import thor.bridge_tournament.core.port.dto.UserDto

interface UserService {
    fun register(playerDto: UserDto): UUID

    fun getAllPlayers(): List<UserDto>

    fun getById(userId: UUID): UserDto

    fun findByUsername(username: String): List<UserDto>

    fun findBySurname(surname: String): List<UserDto>
}
