package thor.bridge_tournament.core.domain.tournament

import java.util.TreeSet

class ImpTranslationScale(nodes: Collection<ImpTranslationNode>) {
    private val nodes: TreeSet<ImpTranslationNode> = TreeSet<ImpTranslationNode>()

    init {
        this.nodes.addAll(nodes)
        var prev: ImpTranslationNode? = null
        for (node in this.nodes) {
            if (prev == null) {
                prev = node
                continue
            }
            if (node.min <= prev.max || node.imps != prev.imps + 1) throw RuntimeException("Invalid imp translation scale")
            prev = node
        }
    }

    fun translate(points: Int): Int {
        if (points < 0) throw RuntimeException("points should be greater then zero")
        val node = nodes.floor(ImpTranslationNode(points, points + 10, 1)) ?: nodes.last()
        return node.imps
    }

    companion object {
        fun createDefault(): ImpTranslationScale {
            return ImpTranslationScale(
                listOf(
                    ImpTranslationNode(0, 10, 0),
                    ImpTranslationNode(20, 40, 1),
                    ImpTranslationNode(50, 80, 2),
                    ImpTranslationNode(90, 120, 3),
                    ImpTranslationNode(130, 160, 4),
                    ImpTranslationNode(170, 210, 5),
                    ImpTranslationNode(220, 260, 6),
                    ImpTranslationNode(270, 310, 7),
                    ImpTranslationNode(320, 360, 8),
                    ImpTranslationNode(370, 420, 9),
                    ImpTranslationNode(430, 490, 10),
                    ImpTranslationNode(500, 590, 11),
                    ImpTranslationNode(600, 740, 12),
                    ImpTranslationNode(750, 890, 13),
                    ImpTranslationNode(900, 1090, 14),
                    ImpTranslationNode(1100, 1290, 15),
                    ImpTranslationNode(1300, 1490, 16)
                )
            )
        }
    }
}
