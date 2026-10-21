package thor.bridge_tournament.core.domain.tournament

import thor.bridge_tournament.core.domain.board.BoardEntry

class MpBoardCalculator : BoardCalculator {
    override fun calculate(entries: List<BoardEntry>): Map<BoardEntry, Double> {
        val result = mutableMapOf<BoardEntry, Double>()
        val boards = entries.groupBy { it.board.id }
        for (boardEntries in boards.values) {
            for (entry in boardEntries) {
                val lower = boardEntries.count { it.points < entry.points }
                val equal = boardEntries.count { it.points == entry.points } - 1
                val percent = if (boardEntries.size == 1)
                    50.0
                else
                    100.0 * (2 * lower + equal) / (2 * (boardEntries.size - 1))
                result[entry] = percent
            }
        }
        return result
    }
}
