package thor.bridge_tournament.infrastructure.repository

import java.util.Optional
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import thor.bridge_tournament.core.port.dto.board.BoardDto
import thor.bridge_tournament.core.port.output.repository.BoardRepository
import thor.bridge_tournament.infrastructure.jpa.repository.BoardJpaRepository
import thor.bridge_tournament.infrastructure.mapping.BoardMapper

@Repository
class BoardRepositoryImpl(
    private val repository: BoardJpaRepository,
    private val mapper: BoardMapper
) : BoardRepository {
    @Transactional
    override fun save(board: BoardDto): Int {
        return requireNotNull(repository.save(mapper.toJpa(board)).id)
    }

    override fun getById(id: Int): Optional<BoardDto> {
        return repository.findById(id).map(mapper::toDto)
    }

    override fun getAllBoards(): List<BoardDto> {
        return repository.findAll().map(mapper::toDto)
    }
}
