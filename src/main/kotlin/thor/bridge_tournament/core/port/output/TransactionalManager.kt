package thor.bridge_tournament.core.port.output

import java.util.function.Supplier

interface TransactionalManager {
    fun <T : Any> executeTransactional(action: Supplier<T>): T

    fun executeTransactional(action: Runnable)
}
