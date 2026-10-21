package thor.bridge_tournament.core.port.dto

import java.util.UUID

class UserDto(
    id: UUID?,
    username: String?,
    name: String?,
    surname: String?,
    sportCategory: Double?
) {
    val id: UUID?
    val username: String?
    val name: String?
    val surname: String?
    val sportCategory: Double?

    init {
        this.id = id
        this.username = username
        this.name = name
        this.surname = surname
        this.sportCategory = sportCategory
    }
}
