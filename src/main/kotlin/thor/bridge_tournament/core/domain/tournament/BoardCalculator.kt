package thor.bridge_tournament.core.domain.tournament

import thor.bridge_tournament.core.domain.board.BoardEntry

interface BoardCalculator {
    fun calculate(entries: List<BoardEntry>): Map<BoardEntry, Double>
}
