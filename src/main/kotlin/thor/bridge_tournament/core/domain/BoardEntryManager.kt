package thor.bridge_tournament.core.domain

import java.util.UUID
import thor.bridge_tournament.core.domain.tournament.BoardCalculator
import thor.bridge_tournament.core.exception.BoardNotFoundException
import thor.bridge_tournament.core.mapping.BoardEntryMapper
import thor.bridge_tournament.core.mapping.BoardMapper
import thor.bridge_tournament.core.port.dto.board.BoardEntryDto
import thor.bridge_tournament.core.port.dto.board.PairBoardResult
import thor.bridge_tournament.core.port.dto.board.RawBoardEntry
import thor.bridge_tournament.core.port.output.repository.BoardEntryRepository
import thor.bridge_tournament.core.port.output.repository.BoardRepository
import thor.bridge_tournament.core.port.output.repository.UserRepository

class BoardEntryManager(
    private val userRepository: UserRepository,
    private val boardEntryRepository: BoardEntryRepository,
    private val boardRepository: BoardRepository
) {
    fun addBoardEntry(
        userId: UUID,
        boardId: Int,
        entry: RawBoardEntry,
        calculator: BoardCalculator,
        countType: String
    ): PairBoardResult {
        val boardEntryId = boardEntryRepository.getEntryId(userId, boardId).orElse(null)
        return saveBoardEntry(userId, boardId, boardEntryId, entry, calculator, countType)
    }

    fun saveBoardEntry(
        userId: UUID,
        boardId: Int,
        boardEntryId: UUID?,
        entry: RawBoardEntry,
        calculator: BoardCalculator,
        countType: String
    ): PairBoardResult {
        val player = userRepository.getById(userId)
        val boardDto = boardRepository.getById(boardId).orElseThrow { BoardNotFoundException(boardId) }
        val board = BoardMapper.fromDto(boardDto)
        val boardEntry = BoardEntryMapper.fromDto(
            BoardEntryDto(
                boardEntryId,
                BoardMapper.toDto(board),
                player,
                entry.contract,
                entry.declarer,
                entry.lead,
                entry.result,
                0
            )
        )
        val newBoardEntryId = boardEntryRepository.save(BoardEntryMapper.toDto(boardEntry))
        val entries = boardEntryRepository.findByBoardId(boardId).map(BoardEntryMapper::fromDto)
        val results = calculator.calculate(entries).mapKeys { BoardEntryMapper.toDto(it.key) }
        return PairBoardResult(
            newBoardEntryId,
            countType,
            boardEntry.points,
            results.entries.first { it.key.id == newBoardEntryId }.value,
            results
        )
    }

    fun getBoardEntries(boardId: Int): List<BoardEntryDto> {
        boardRepository.getById(boardId).orElseThrow { BoardNotFoundException(boardId) }
        return boardEntryRepository.findByBoardId(boardId)
            .map(BoardEntryMapper::fromDto)
            .map(BoardEntryMapper::toDto)
    }
}
