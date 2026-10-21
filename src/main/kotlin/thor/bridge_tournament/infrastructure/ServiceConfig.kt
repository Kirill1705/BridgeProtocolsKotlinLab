package thor.bridge_tournament.infrastructure

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import thor.bridge_tournament.core.domain.BoardEntryManager
import thor.bridge_tournament.core.port.input.BoardEntryService
import thor.bridge_tournament.core.port.input.BoardService
import thor.bridge_tournament.core.port.input.UserService
import thor.bridge_tournament.core.port.output.TransactionalManager
import thor.bridge_tournament.core.port.output.repository.BoardEntryRepository
import thor.bridge_tournament.core.port.output.repository.BoardRepository
import thor.bridge_tournament.core.port.output.repository.UserRepository
import thor.bridge_tournament.core.service.BoardEntryServiceImpl
import thor.bridge_tournament.core.service.BoardServiceImpl
import thor.bridge_tournament.core.service.UserServiceImpl

@Configuration
class ServiceConfig {
    @Bean
    fun boardService(repository: BoardRepository): BoardService {
        return BoardServiceImpl(repository)
    }

    @Bean
    fun userService(userRepository: UserRepository): UserService {
        return UserServiceImpl(userRepository)
    }

    @Bean
    fun boardEntryManager(
        userRepository: UserRepository,
        boardEntryRepository: BoardEntryRepository,
        boardRepository: BoardRepository
    ): BoardEntryManager {
        return BoardEntryManager(userRepository, boardEntryRepository, boardRepository)
    }

    @Bean
    fun boardEntryService(
        boardEntryManager: BoardEntryManager,
        transactionalManager: TransactionalManager
    ): BoardEntryService {
        return BoardEntryServiceImpl(boardEntryManager, transactionalManager)
    }
}
