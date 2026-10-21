package thor.bridge_tournament.core.domain.board

import thor.bridge_tournament.core.domain.Contract

interface BoardCalculator {
    fun calculate(board: Board, declarer: Direction?, contract: Contract, result: Int): Int
}
