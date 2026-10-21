package thor.bridge_tournament.core.service

import thor.bridge_tournament.core.domain.board.Board
import thor.bridge_tournament.core.exception.BoardNotFoundException
import thor.bridge_tournament.core.mapping.BoardMapper
import thor.bridge_tournament.core.port.dto.board.BoardDto
import thor.bridge_tournament.core.port.input.BoardService
import thor.bridge_tournament.core.port.output.repository.BoardRepository

class BoardServiceImpl(private val boardRepository: BoardRepository) : BoardService {
    override fun addBoard(number: Int): Int {
        val board = Board(number)
        return boardRepository.save(BoardMapper.toDto(board))
    }

    override fun getById(boardId: Int): BoardDto {
        return boardRepository.getById(boardId).orElseThrow { BoardNotFoundException(boardId) }
    }

    override fun getAllBoards(): List<BoardDto> {
        return boardRepository.getAllBoards()
    }
}
