package thor.bridge_tournament.presentation.rest.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import thor.bridge_tournament.core.port.dto.board.RawBoardEntry

data class BoardEntryRequest(
    @field:NotBlank val contract: String?,
    val declarer: String?,
    val lead: String?,
    @field:NotNull val result: Int?
) {
    fun toDto(): RawBoardEntry {
        return RawBoardEntry(requireNotNull(contract), declarer, lead, requireNotNull(result))
    }
}
