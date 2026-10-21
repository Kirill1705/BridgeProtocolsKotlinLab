package thor.bridge_tournament.core.service

import java.util.UUID
import java.util.function.Supplier
import thor.bridge_tournament.core.domain.BoardEntryManager
import thor.bridge_tournament.core.domain.tournament.CountType
import thor.bridge_tournament.core.domain.tournament.ImpTranslationScale
import thor.bridge_tournament.core.domain.tournament.MedianImpsCalculator
import thor.bridge_tournament.core.domain.tournament.MpBoardCalculator
import thor.bridge_tournament.core.port.dto.board.BoardEntryDto
import thor.bridge_tournament.core.port.dto.board.PairBoardResult
import thor.bridge_tournament.core.port.dto.board.RawBoardEntry
import thor.bridge_tournament.core.port.input.BoardEntryService
import thor.bridge_tournament.core.port.output.TransactionalManager

class BoardEntryServiceImpl(
    private val boardEntryManager: BoardEntryManager,
    private val transactionalManager: TransactionalManager
) : BoardEntryService {
    override fun addBoardEntryImps(
        userId: UUID,
        boardId: Int,
        entry: RawBoardEntry,
        throwAwayPercent: Double?
    ): PairBoardResult {
        val calculator = MedianImpsCalculator(throwAwayPercent ?: 0.0, ImpTranslationScale.createDefault())
        return transactionalManager.executeTransactional(Supplier {
            boardEntryManager.addBoardEntry(userId, boardId, entry, calculator, CountType.MEDIAN_IMPS.formatStandardName)
        })
    }

    override fun addBoardEntryMp(userId: UUID, boardId: Int, entry: RawBoardEntry): PairBoardResult {
        val calculator = MpBoardCalculator()
        return transactionalManager.executeTransactional(Supplier {
            boardEntryManager.addBoardEntry(userId, boardId, entry, calculator, CountType.MP.formatStandardName)
        })
    }

    override fun getBoardEntries(boardId: Int): List<BoardEntryDto> {
        return boardEntryManager.getBoardEntries(boardId)
    }
}
