package thor.bridge_tournament.core.domain.board

import thor.bridge_tournament.core.domain.Contract

class WinLink : BaseBoardCalculatorLink() {
    override fun calculate(
        board: Board,
        declarer: Direction?,
        contract: Contract,
        result: Int
    ): Int {
        if (result >= 0) {
            if (contract.denomination == null) {
                return 0
            }
            val vulnerable: Boolean = isVulnerable(requireNotNull(declarer), board.vulnerable)
            val basePrice: Int = contract.denomination.getBasePrice(contract.level) * contract.modifier.multiplier
            var points: Int = getPrice(contract, result, basePrice, vulnerable) + contract.modifier.pointsIfWin
            if (requireNotNull(declarer).isEW) {
                points = -points
            }
            return points
        }
        return callNext(board, declarer, contract, result)
    }

    private fun getPrice(
        contract: Contract,
        result: Int,
        basePrice: Int,
        vulnerable: Boolean
    ): Int {
        var price = basePrice
        if (basePrice >= 100) {
            price += gamePoints(vulnerable)
            if (contract.level == 6) {
                price += littleSlamPoints(vulnerable)
            } else if (contract.level == 7) {
                price += slamPoints(vulnerable)
            }
        } else {
            price += 50
        }
        if (contract.modifier == Modifier.PASS) {
            price = price + (result * requireNotNull(contract.denomination).basePrice)
        } else {
            val m = if (vulnerable) 2 else 1
            price = price + (result * contract.modifier.additionalPointsPerTrick * m)
        }
        return price
    }

    private fun gamePoints(vulnerable: Boolean): Int {
        if (vulnerable) {
            return 500
        }
        return 300
    }

    private fun littleSlamPoints(vulnerable: Boolean): Int {
        if (vulnerable) {
            return 750
        }
        return 500
    }

    private fun slamPoints(vulnerable: Boolean): Int {
        if (vulnerable) {
            return 1500
        }
        return 1000
    }
}
