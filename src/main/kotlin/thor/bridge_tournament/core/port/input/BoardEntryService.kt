package thor.bridge_tournament.core.port.input

import java.util.UUID
import thor.bridge_tournament.core.port.dto.board.BoardEntryDto
import thor.bridge_tournament.core.port.dto.board.PairBoardResult
import thor.bridge_tournament.core.port.dto.board.RawBoardEntry

interface BoardEntryService {
    fun addBoardEntryImps(userId: UUID, boardId: Int, entry: RawBoardEntry, throwAwayPercent: Double?): PairBoardResult

    fun addBoardEntryMp(userId: UUID, boardId: Int, entry: RawBoardEntry): PairBoardResult

    fun getBoardEntries(boardId: Int): List<BoardEntryDto>
}
