package thor.bridge_tournament.core.service

import java.util.UUID
import thor.bridge_tournament.core.mapping.UserMapper
import thor.bridge_tournament.core.port.dto.UserDto
import thor.bridge_tournament.core.port.input.UserService
import thor.bridge_tournament.core.port.output.repository.UserRepository

class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override fun register(playerDto: UserDto): UUID {
        if (playerDto.id != null) {
            userRepository.getById(playerDto.id)
        }
        val user = UserMapper.fromDto(playerDto)
        return userRepository.addUser(UserMapper.toDto(user))
    }

    override fun getAllPlayers(): List<UserDto> {
        return userRepository.allPlayers()
    }

    override fun getById(userId: UUID): UserDto {
        return userRepository.getById(userId)
    }

    override fun findByUsername(username: String): List<UserDto> {
        return userRepository.findByUsername(username)
    }

    override fun findBySurname(surname: String): List<UserDto> {
        return userRepository.findBySurname(surname)
    }
}
