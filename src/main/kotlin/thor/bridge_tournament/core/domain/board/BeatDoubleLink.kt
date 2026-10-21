package thor.bridge_tournament.core.domain.board

import thor.bridge_tournament.core.domain.Contract

class BeatDoubleLink : BaseBoardCalculatorLink() {
    private val nonVulnerable: List<Int> = listOf(100, 300, 500)
    private val vulnerable: List<Int> = listOf(200, 500)

    override fun calculate(
        board: Board,
        declarer: Direction?,
        contract: Contract,
        result: Int
    ): Int {
        if (result < 0 && contract.modifier != Modifier.PASS) {
            var points: Int
            if (isVulnerable(requireNotNull(declarer), board.vulnerable)) {
                points = calculateVulnerable(-result, vulnerable)
            } else {
                points = calculateVulnerable(-result, nonVulnerable)
            }
            if (contract.modifier == Modifier.REDOUBLE) {
                points *= 2
            }
            if (requireNotNull(declarer).isNS) {
                points = -points
            }
            return points
        }
        return callNext(board, declarer, contract, result)
    }

    private fun calculateVulnerable(absResult: Int, results: List<Int>): Int {
        if (absResult <= results.size) {
            return results[absResult - 1]
        }
        return results.last() + 300 * (absResult - results.size)
    }
}
