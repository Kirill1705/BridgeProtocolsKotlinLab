package thor.bridge_tournament.core.port.dto.board

import java.util.UUID
import thor.bridge_tournament.core.port.dto.UserDto

data class BoardEntryDto(
    val id: UUID?,
    val board: BoardDto,
    val writer: UserDto,
    val contract: String,
    val declarer: String?,
    val lead: String?,
    val result: Int,
    val points: Int
)
