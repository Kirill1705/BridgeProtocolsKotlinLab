package thor.bridge_tournament.core.exception

import java.util.UUID

class UserNotFoundException(userId: UUID?) :
    RuntimeException("User with ID " + userId + " not found")
