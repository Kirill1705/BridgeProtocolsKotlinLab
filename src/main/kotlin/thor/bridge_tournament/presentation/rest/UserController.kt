package thor.bridge_tournament.presentation.rest

import jakarta.validation.Valid
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import thor.bridge_tournament.core.port.dto.UserDto
import thor.bridge_tournament.core.port.input.UserService
import thor.bridge_tournament.presentation.rest.dto.UserRequest

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: UserRequest): UUID {
        return userService.register(request.toDto())
    }

    @GetMapping("/{userId}")
    fun getById(@PathVariable userId: UUID): UserDto {
        return userService.getById(userId)
    }

    @GetMapping
    fun getAll(): List<UserDto> {
        return userService.getAllPlayers()
    }

    @GetMapping("/by-surname")
    fun findBySurname(@RequestParam surname: String): List<UserDto> {
        return userService.findBySurname(surname)
    }

    @GetMapping("/by-username")
    fun findByUsername(@RequestParam username: String): List<UserDto> {
        return userService.findByUsername(username)
    }
}
