package thor.bridge_tournament.infrastructure.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import thor.bridge_tournament.infrastructure.jpa.BoardEntity

interface BoardJpaRepository : JpaRepository<BoardEntity, Int>
