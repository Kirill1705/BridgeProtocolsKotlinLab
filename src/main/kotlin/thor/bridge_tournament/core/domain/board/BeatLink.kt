package thor.bridge_tournament.core.domain.board

import thor.bridge_tournament.core.domain.Contract

class BeatLink : BaseBoardCalculatorLink() {
    override fun calculate(
        board: Board,
        declarer: Direction?,
        contract: Contract,
        result: Int
    ): Int {
        if (result < 0 && contract.modifier == Modifier.PASS) {
            var points: Int
            if (isVulnerable(requireNotNull(declarer), board.vulnerable)) {
                points = -result * 100
            } else {
                points = -result * 50
            }
            if (requireNotNull(declarer).isNS) {
                points = -points
            }
            return points
        }
        return callNext(board, declarer, contract, result)
    }
}
