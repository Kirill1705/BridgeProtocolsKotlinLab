package thor.bridge_tournament.presentation.rest

import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import thor.bridge_tournament.core.port.dto.board.BoardDto
import thor.bridge_tournament.core.port.input.BoardService
import thor.bridge_tournament.presentation.rest.dto.BoardRequest

@RestController
@RequestMapping("/boards")
class BoardController(private val boardService: BoardService) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBoard(@Valid @RequestBody request: BoardRequest): Int {
        return boardService.addBoard(request.number)
    }

    @GetMapping("/{boardId}")
    fun getById(@PathVariable @Positive boardId: Int): BoardDto {
        return boardService.getById(boardId)
    }

    @GetMapping
    fun getAll(): List<BoardDto> {
        return boardService.getAllBoards()
    }
}
