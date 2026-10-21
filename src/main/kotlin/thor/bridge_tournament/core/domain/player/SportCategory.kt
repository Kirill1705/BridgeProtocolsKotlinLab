package thor.bridge_tournament.core.domain.player

import thor.bridge_tournament.core.exception.DomainValidationException

data class SportCategory(val value: Double) {
    init {
        if (!value.isFinite() || value < -5 || value > 5) {
            throw DomainValidationException("sport category must be between -5 and 5")
        }
    }

    constructor(value: Double?) : this(value ?: 5.0)
}
