package thor.bridge_tournament.core.mapping

import java.util.Locale
import thor.bridge_tournament.core.domain.Contract
import thor.bridge_tournament.core.domain.board.BoardEntry
import thor.bridge_tournament.core.domain.board.Direction
import thor.bridge_tournament.core.domain.card.Card
import thor.bridge_tournament.core.port.dto.board.BoardEntryDto

object BoardEntryMapper {
    fun toDto(entry: BoardEntry): BoardEntryDto {
        return BoardEntryDto(
            entry.uuid,
            BoardMapper.toDto(entry.board),
            UserMapper.toDto(requireNotNull(entry.writer)),
            entry.contract.toStandardName(),
            entry.declarer?.name,
            entry.lead?.toName(),
            entry.result,
            entry.points
        )
    }

    fun fromDto(dto: BoardEntryDto): BoardEntry {
        return BoardEntry(
            dto.id,
            BoardMapper.fromDto(dto.board),
            Contract(dto.contract.uppercase(Locale.ROOT)),
            dto.declarer?.let { Direction.valueOf(it.uppercase(Locale.ROOT)) },
            dto.lead?.let { Card(it.uppercase(Locale.ROOT)) },
            dto.result,
            UserMapper.fromDto(dto.writer)
        )
    }
}
