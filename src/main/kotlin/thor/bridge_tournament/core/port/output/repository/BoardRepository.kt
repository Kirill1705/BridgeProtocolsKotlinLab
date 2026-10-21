package thor.bridge_tournament.core.port.output.repository

import java.util.Optional
import thor.bridge_tournament.core.port.dto.board.BoardDto

interface BoardRepository {
    fun save(board: BoardDto): Int

    fun getById(id: Int): Optional<BoardDto>

    fun getAllBoards(): List<BoardDto>
}
