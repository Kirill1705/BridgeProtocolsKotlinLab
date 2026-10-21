package thor.bridge_tournament.infrastructure.repository

import java.util.Optional
import java.util.UUID
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import thor.bridge_tournament.core.port.dto.board.BoardEntryDto
import thor.bridge_tournament.core.port.output.repository.BoardEntryRepository
import thor.bridge_tournament.infrastructure.jpa.repository.BoardEntryJpaRepository
import thor.bridge_tournament.infrastructure.mapping.BoardEntryMapper

@Repository
class BoardEntryRepositoryImpl(
    private val repository: BoardEntryJpaRepository,
    private val mapper: BoardEntryMapper
) : BoardEntryRepository {
    override fun getEntryId(writerId: UUID, boardId: Int): Optional<UUID> {
        return repository.findEntryIdByWriterIdAndBoardId(writerId, boardId)
    }

    @Transactional
    override fun save(boardEntry: BoardEntryDto): UUID {
        return requireNotNull(repository.save(mapper.toJpa(boardEntry)).id)
    }

    override fun findByBoardId(boardId: Int): List<BoardEntryDto> {
        return repository.findByBoardId(boardId).map(mapper::toDto)
    }
}
