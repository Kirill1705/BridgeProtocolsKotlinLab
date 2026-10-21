package thor.bridge_tournament.core.port.dto.board

data class RawBoardEntry(val contract: String, val declarer: String?, val lead: String?, val result: Int)
