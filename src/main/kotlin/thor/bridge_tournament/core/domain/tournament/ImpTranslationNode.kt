package thor.bridge_tournament.core.domain.tournament

import thor.bridge_tournament.core.exception.DomainValidationException

data class ImpTranslationNode(val min: Int, val max: Int, val imps: Int) : Comparable<ImpTranslationNode> {
    override fun compareTo(other: ImpTranslationNode): Int {
        return java.lang.Integer.compare(min, other.min)
    }

    init {
        if (min >= max) throw DomainValidationException("max should be greater then min")
        if (min < 0 || imps < 0) throw DomainValidationException("imps and points should be greater then zero")
    }
}
