package thor.bridge_tournament.core.domain

import java.util.Locale
import thor.bridge_tournament.core.domain.board.Modifier
import thor.bridge_tournament.core.exception.DomainValidationException

data class Contract(val denomination: Denomination?, val level: Int, val modifier: Modifier) {
    init {
        if (level < 0 || level > 7 || (denomination != null && level == 0)) {
            throw DomainValidationException("Contract level must be between 1 and 7")
        }
    }

    constructor(name: String) : this(parse(name))

    private constructor(parsed: Contract) : this(parsed.denomination, parsed.level, parsed.modifier)

    fun toStandardName(): String {
        if (denomination == null) {
            return Modifier.PASS.name.lowercase(Locale.ROOT)
        }
        return level.toString() + denomination.name + modifier.toStandardName()
    }

    companion object {
        private fun parse(value: String): Contract {
            val name = value.uppercase(Locale.ROOT)
            if (name == Modifier.PASS.name) {
                return Contract(null, 0, Modifier.PASS)
            }

            val level = name.substring(0, 1).toInt()
            var xxIndex = name.indexOf('X')
            if (xxIndex == -1) {
                xxIndex = name.length
            }
            val denomination = Denomination.valueOf(name.substring(1, xxIndex))
            val modifier = when (name.length - xxIndex) {
                0 -> Modifier.PASS
                1 -> Modifier.DOUBLE
                2 -> Modifier.REDOUBLE
                else -> throw IllegalStateException(name)
            }
            return Contract(denomination, level, modifier)
        }
    }
}
