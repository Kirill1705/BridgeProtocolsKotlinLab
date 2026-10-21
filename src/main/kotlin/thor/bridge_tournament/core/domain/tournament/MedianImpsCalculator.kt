package thor.bridge_tournament.core.domain.tournament

import kotlin.math.abs
import thor.bridge_tournament.core.domain.board.BoardEntry
import thor.bridge_tournament.core.exception.DomainValidationException

class MedianImpsCalculator(
    private val throwAway: Double,
    private val translationScale: ImpTranslationScale
) : BoardCalculator {
    init {
        if (!throwAway.isFinite() || throwAway < 0 || throwAway >= 0.5) {
            throw DomainValidationException("The value for ignoring unusual results should be between 0 and 0.5")
        }
    }

    constructor(translationScale: ImpTranslationScale) : this(0.0, translationScale)

    override fun calculate(entries: List<BoardEntry>): Map<BoardEntry, Double> {
        val result = mutableMapOf<BoardEntry, Double>()
        val grouped = groupByBoard(entries)
        for (entryList in grouped) {
            val avg = countAverage(entryList)
            for (entry in entryList) {
                val diff = entry.points - avg
                var imps = translationScale.translate(abs(diff))
                if (diff < 0) imps = -imps
                result[entry] = imps.toDouble()
            }
        }
        return result
    }

    private fun groupByBoard(entries: List<BoardEntry>): Collection<List<BoardEntry>> {
        return entries.groupBy { it.board.id }.values
    }

    private fun countAverage(entries: List<BoardEntry>): Int {
        val realEntries = entries.sortedBy { it.points }
        val throwCount = (realEntries.size * throwAway).toInt()
        val count = realEntries.size - throwCount * 2
        var sum = 0
        for (i in throwCount until realEntries.size - throwCount) {
            sum += realEntries[i].points
        }
        return sum / count
    }
}
