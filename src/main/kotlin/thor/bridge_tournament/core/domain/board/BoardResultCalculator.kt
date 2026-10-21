package thor.bridge_tournament.core.domain.board

import thor.bridge_tournament.core.domain.Contract

enum class BoardResultCalculator {
    VALUE;

    private val calculator: BoardCalculator

    init {
        calculator = WinLink()
            .addNext(BeatLink())
            .addNext(BeatDoubleLink())
    }

    fun calculate(
        board: Board,
        direction: Direction?,
        contract: Contract,
        result: Int
    ): Int {
        return calculator.calculate(board, direction, contract, result)
    }
}
