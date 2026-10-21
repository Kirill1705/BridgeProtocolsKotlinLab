package thor.bridge_tournament.core.exception

class BoardNotFoundException(val boardNumber: Int) :
    RuntimeException("Board with ID " + boardNumber + " not found")
