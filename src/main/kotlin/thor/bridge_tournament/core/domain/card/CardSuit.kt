package thor.bridge_tournament.core.domain.card

enum class CardSuit {
    CLUBS,
    DIAMONDS,
    HEARTS,
    SPADES;

    fun toStandardName(): String {
        return when (this) {
            CLUBS -> "c"
            DIAMONDS -> "d"
            HEARTS -> "h"
            SPADES -> "s"
        }
    }

    companion object {
        fun fromStandardName(name: String): CardSuit {
            return when (name) {
                "s" -> SPADES
                "c" -> CLUBS
                "d" -> DIAMONDS
                "h" -> HEARTS
                else -> throw IllegalStateException(name)
            }
        }
    }
}
