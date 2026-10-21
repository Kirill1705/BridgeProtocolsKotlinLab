package thor.bridge_tournament.core.domain.card

import java.util.Locale

data class Card(val nominal: CardNominal, val suit: CardSuit) {
    constructor(name: String) : this(
        CardNominal.fromName(name.substring(1).uppercase(Locale.ROOT)),
        CardSuit.fromStandardName(name.substring(0, 1).lowercase(Locale.ROOT))
    )

    fun toName(): String {
        return suit.toStandardName() + nominal.toStandardName()
    }
}
