package thor.bridge_tournament.core.domain.board

import thor.bridge_tournament.core.domain.Contract

abstract class BaseBoardCalculatorLink : BoardCalculatorLink {
    private var next: BoardCalculatorLink? = null

    override fun addNext(link: BoardCalculatorLink): BoardCalculatorLink {
        val nextLink = next
        if (nextLink == null) {
            next = link
        } else {
            nextLink.addNext(link)
        }
        return this
    }

    protected fun callNext(board: Board, declarer: Direction?, contract: Contract, result: Int): Int {
        val nextLink = next ?: throw RuntimeException("Cant calculate board result")
        return nextLink.calculate(board, declarer, contract, result)
    }

    protected fun isVulnerable(declarer: Direction, vulnerable: Vulnerable): Boolean {
        if (declarer.isNS) {
            return vulnerable == Vulnerable.NS || vulnerable == Vulnerable.ALL
        }
        return vulnerable == Vulnerable.EW || vulnerable == Vulnerable.ALL
    }
}
