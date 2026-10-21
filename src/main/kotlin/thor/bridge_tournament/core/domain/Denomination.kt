package thor.bridge_tournament.core.domain

enum class Denomination {
    C,
    D,
    H,
    S,
    NT;

    fun getBasePrice(tricks: Int): Int {
        return when (this) {
            C, D -> 20 * tricks
            H, S -> 30 * tricks
            NT -> 30 * tricks + 10
        }
    }

    val basePrice: Int
        get() = when (this) {
            C, D -> 20
            else -> 30
        }
}
