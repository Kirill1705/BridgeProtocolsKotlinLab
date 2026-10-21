package thor.bridge_tournament.core.mapping

import thor.bridge_tournament.core.domain.board.Board
import thor.bridge_tournament.core.domain.board.Direction
import thor.bridge_tournament.core.domain.board.Vulnerable
import thor.bridge_tournament.core.port.dto.board.BoardDto

object BoardMapper {
    fun toDto(board: Board): BoardDto {
        return BoardDto(
            board.id,
            board.number,
            board.dealer.name.lowercase(),
            board.vulnerable.toString()
        )
    }

    fun fromDto(boardDto: BoardDto): Board {
        return Board(
            boardDto.id,
            boardDto.number,
            Direction.valueOf(boardDto.dealer.uppercase()),
            Vulnerable.fromName(boardDto.vulnerable.uppercase())
        )
    }
}
