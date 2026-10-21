package thor.bridge_tournament.core.domain.board

import thor.bridge_tournament.core.exception.DomainValidationException

class Board(val id: Int?, val number: Int, val dealer: Direction, val vulnerable: Vulnerable) {
    init {
        validateNumber(number)
    }

    constructor(number: Int) : this(
        null, number, dealerFor(number), vulnerabilityFor(number)
    )

    companion object {
        private fun validateNumber(number: Int) {
            if (number <= 0) {
                throw DomainValidationException("board number should be greater then zero")
            }
        }

        private fun dealerFor(number: Int): Direction {
            validateNumber(number)
            return Direction.entries[(number - 1) % 4]
        }

        private fun vulnerabilityFor(number: Int): Vulnerable {
            validateNumber(number)
            val shift = ((number - 1) / 4) % 4
            return Vulnerable.entries[(number - 1 + shift) % 4]
        }
    }
}
