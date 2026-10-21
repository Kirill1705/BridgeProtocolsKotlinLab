package thor.bridge_tournament.infrastructure

import java.util.function.Supplier
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import thor.bridge_tournament.core.port.output.TransactionalManager

@Component
class TransactionalManagerImpl(private val transactionTemplate: TransactionTemplate) : TransactionalManager {
    override fun <T : Any> executeTransactional(action: Supplier<T>): T {
        return requireNotNull(transactionTemplate.execute { action.get() })
    }

    override fun executeTransactional(action: Runnable) {
        transactionTemplate.executeWithoutResult { action.run() }
    }
}
