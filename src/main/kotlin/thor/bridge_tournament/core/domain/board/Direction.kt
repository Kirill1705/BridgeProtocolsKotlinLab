package thor.bridge_tournament.core.domain.board

enum class Direction {
    N,
    E,
    S,
    W;

    val isNS: Boolean
        get() = this == Direction.N || this == Direction.S

    val isEW: Boolean
        get() = !this.isNS
}
