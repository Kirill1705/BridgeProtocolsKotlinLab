package thor.bridge_tournament.core.port.output.repository

import java.util.Optional
import java.util.UUID
import thor.bridge_tournament.core.port.dto.board.BoardEntryDto

interface BoardEntryRepository {
    fun getEntryId(writerId: UUID, boardId: Int): Optional<UUID>

    fun save(boardEntry: BoardEntryDto): UUID

    fun findByBoardId(boardId: Int): List<BoardEntryDto>
}
