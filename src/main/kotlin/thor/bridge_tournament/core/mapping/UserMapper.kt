package thor.bridge_tournament.core.mapping

import thor.bridge_tournament.core.domain.player.SportCategory
import thor.bridge_tournament.core.domain.player.User
import thor.bridge_tournament.core.port.dto.UserDto

object UserMapper {
    fun toDto(user: User): UserDto {
        return UserDto(
            user.uuid(),
            user.username(),
            user.name(),
            user.surname(),
            user.category().value
        )
    }

    fun fromDto(dto: UserDto): User {
        return User(
            dto.id,
            dto.name,
            dto.surname,
            SportCategory(dto.sportCategory),
            dto.username
        )
    }
}
