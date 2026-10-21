package thor.bridge_tournament.core.domain.board

import java.util.UUID
import thor.bridge_tournament.core.domain.Contract
import thor.bridge_tournament.core.domain.card.Card
import thor.bridge_tournament.core.domain.player.User
import thor.bridge_tournament.core.exception.DomainValidationException

class BoardEntry(
    val uuid: UUID?,
    val board: Board,
    contract: Contract?,
    declarer: Direction?,
    lead: Card?,
    result: Int,
    val writer: User?
) {
    lateinit var contract: Contract
        private set
    var declarer: Direction? = null
        private set
    var lead: Card? = null
        private set
    var result: Int = 0
        private set
    var points: Int = 0
        private set

    init {
        setResult(contract, declarer, lead, result)
    }

    fun setResult(contract: Contract?, declarer: Direction?, lead: Card?, result: Int) {
        if (contract == null) {
            throw DomainValidationException("Contract is required")
        }
        if (contract.denomination == null && result != 0) {
            throw DomainValidationException("A passed-out board must have result zero")
        }
        if (contract.denomination != null && declarer == null) {
            throw DomainValidationException("Declarer is required for a played contract")
        }
        val minResult = -(contract.level + 6)
        val maxResult = minResult + 13
        if (result < minResult || result > maxResult) {
            throw DomainValidationException("Result should be between $minResult and $maxResult")
        }
        points = BoardResultCalculator.VALUE.calculate(board, declarer, contract, result)
        this.contract = contract
        this.declarer = declarer
        this.lead = lead
        this.result = result
    }
}
