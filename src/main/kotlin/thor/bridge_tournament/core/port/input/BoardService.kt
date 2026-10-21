package thor.bridge_tournament.core.port.input

import thor.bridge_tournament.core.port.dto.board.BoardDto

interface BoardService {
    fun addBoard(number: Int): Int

    fun getById(boardId: Int): BoardDto

    fun getAllBoards(): List<BoardDto>
}
