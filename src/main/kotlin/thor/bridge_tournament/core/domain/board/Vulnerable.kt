package thor.bridge_tournament.core.domain.board

import java.util.Locale

enum class Vulnerable {
    NO_ONE,
    NS,
    EW,
    ALL;

    override fun toString(): String {
        if (this == NO_ONE) {
            return "-"
        }
        return super.toString()
    }

    companion object {
        fun fromName(name: String): Vulnerable {
            if (name == "-") {
                return NO_ONE
            }
            return Vulnerable.valueOf(name.uppercase(Locale.getDefault()))
        }
    }
}
