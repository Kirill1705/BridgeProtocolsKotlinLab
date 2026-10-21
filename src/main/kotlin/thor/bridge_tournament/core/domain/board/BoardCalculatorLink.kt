package thor.bridge_tournament.core.domain.board

interface BoardCalculatorLink : BoardCalculator {
    fun addNext(link: BoardCalculatorLink): BoardCalculatorLink
}
