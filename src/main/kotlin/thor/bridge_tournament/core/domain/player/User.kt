package thor.bridge_tournament.core.domain.player

import java.util.UUID

class User(uuid: UUID?, name: String?, surname: String?, category: SportCategory, username: String?) {
    private var uuid: UUID?
    private val name: String?
    private val surname: String?
    private val category: SportCategory
    private val username: String?

    init {
        this.uuid = uuid
        this.name = name
        this.surname = surname
        this.category = category
        this.username = username
    }

    fun uuid(): UUID? {
        return uuid
    }

    fun name(): String? {
        return name
    }

    fun surname(): String? {
        return surname
    }

    fun category(): SportCategory {
        return category
    }

    fun username(): String? {
        return username
    }

    fun setUuid(uuid: UUID?) {
        this.uuid = uuid
    }
}
