package thor.bridge_tournament.core.domain.card

import thor.bridge_tournament.core.exception.DomainValidationException

enum class CardNominal {
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING,
    ACE;

    fun toStandardName(): String {
        if (this.ordinal <= 8) {
            return (this.ordinal + 2).toString()
        }
        return this.name.substring(0, 1)
    }

    companion object {
        fun fromName(name: String): CardNominal {
            if (java.lang.Character.isDigit(name.get(0))) {
                val number: Int = name.toInt()
                if (number < 2 || number > 10) {
                    throw DomainValidationException("Card number must be between 2 and 10")
                }
                return CardNominal.entries[number - 2]
            }
            return when (name) {
                "J" -> JACK
                "Q" -> QUEEN
                "K" -> KING
                "A" -> ACE
                else -> throw IllegalStateException("Unexpected value: " + name)
            }
        }
    }
}
