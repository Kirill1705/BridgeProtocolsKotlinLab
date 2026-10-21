package thor.bridge_tournament.infrastructure.jpa.repository

import java.util.Optional
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import thor.bridge_tournament.infrastructure.jpa.BoardEntryEntity

interface BoardEntryJpaRepository : JpaRepository<BoardEntryEntity, UUID> {
    @Query("SELECT be.id FROM BoardEntryEntity be WHERE be.writer.id = :writerId AND be.board.id = :boardId")
    fun findEntryIdByWriterIdAndBoardId(writerId: UUID, boardId: Int): Optional<UUID>

    fun findByBoardId(boardId: Int): List<BoardEntryEntity>
}
