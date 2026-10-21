package thor.bridge_tournament.core.domain.tournament

enum class CountType {
    MEDIAN_IMPS,
    MP;

    val formatStandardName: String
        get() = when (this) {
            MEDIAN_IMPS -> "IMP"
            MP -> "MP"
        }

    companion object {
        fun fromStandardName(name: String): CountType {
            return when (name) {
                "IMP" -> MEDIAN_IMPS
                "MP" -> MP
                else -> throw IllegalStateException(name)
            }
        }
    }
}
