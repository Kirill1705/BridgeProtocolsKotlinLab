package thor.bridge_tournament.presentation.rest.dto

import java.util.UUID
import thor.bridge_tournament.core.port.dto.board.BoardEntryDto
import thor.bridge_tournament.core.port.dto.board.PairBoardResult

data class BoardEntryResponse(
    val entryId: UUID,
    val countType: String,
    val points: Int,
    val duplicatePoints: Double,
    val protocol: List<ProtocolRow>
) {
    data class ProtocolRow(val entry: BoardEntryDto, val duplicatePoints: Double)

    companion object {
        fun fromDto(result: PairBoardResult): BoardEntryResponse {
            val protocol = result.protocol.entries
                .map { ProtocolRow(it.key, it.value) }
                .sortedBy { it.entry.id }
            return BoardEntryResponse(
                result.entryId, result.countType, result.points,
                result.duplicatePoints, protocol
            )
        }
    }
}
