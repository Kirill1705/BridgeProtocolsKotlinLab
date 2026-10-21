package thor.bridge_tournament.presentation.rest

import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import java.util.UUID
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import thor.bridge_tournament.core.port.dto.board.BoardEntryDto
import thor.bridge_tournament.core.port.input.BoardEntryService
import thor.bridge_tournament.presentation.rest.dto.BoardEntryRequest
import thor.bridge_tournament.presentation.rest.dto.BoardEntryResponse

@RestController
@RequestMapping("/boards/{boardId}/entries")
class BoardEntryController(private val boardEntryService: BoardEntryService) {
    @PostMapping("/{userId}/mp")
    fun addBoardEntryMp(
        @PathVariable @Positive boardId: Int,
        @PathVariable userId: UUID,
        @Valid @RequestBody request: BoardEntryRequest
    ): BoardEntryResponse {
        return BoardEntryResponse.fromDto(boardEntryService.addBoardEntryMp(userId, boardId, request.toDto()))
    }

    @PostMapping("/{userId}/imps")
    fun addBoardEntryImps(
        @PathVariable @Positive boardId: Int,
        @PathVariable userId: UUID,
        @Valid @RequestBody request: BoardEntryRequest,
        @RequestParam(required = false) throwAwayPercent: Double?
    ): BoardEntryResponse {
        return BoardEntryResponse.fromDto(
            boardEntryService.addBoardEntryImps(
                userId,
                boardId,
                request.toDto(),
                throwAwayPercent
            )
        )
    }

    @GetMapping
    fun getProtocol(@PathVariable @Positive boardId: Int): List<BoardEntryDto> {
        return boardEntryService.getBoardEntries(boardId)
    }
}
