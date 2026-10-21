package thor.bridge_tournament.core.domain.board

enum class Modifier {
    DOUBLE,
    REDOUBLE,
    PASS;

    val multiplier: Int
        get() = when (this) {
            Modifier.PASS -> 1
            Modifier.DOUBLE -> 2
            Modifier.REDOUBLE -> 4
        }

    val pointsIfWin: Int
        get() = when (this) {
            Modifier.PASS -> 0
            Modifier.DOUBLE -> 50
            Modifier.REDOUBLE -> 100
        }

    val additionalPointsPerTrick: Int
        get() = when (this) {
            Modifier.PASS -> 0
            Modifier.DOUBLE -> 100
            Modifier.REDOUBLE -> 200
        }

    fun toStandardName(): String {
        return when (this) {
            Modifier.PASS -> ""
            Modifier.DOUBLE -> "x"
            Modifier.REDOUBLE -> "xx"
        }
    }
}
