package thor.bridge_tournament.core.port.dto.board

import java.util.UUID

data class PairBoardResult(
    val entryId: UUID,
    val countType: String,
    val points: Int,
    val duplicatePoints: Double,
    val protocol: Map<BoardEntryDto, Double>
)
