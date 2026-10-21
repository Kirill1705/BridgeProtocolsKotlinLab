package thor.bridge_tournament.presentation.rest.dto

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import thor.bridge_tournament.core.port.dto.UserDto

data class UserRequest(
    val username: String?,
    @field:NotBlank val name: String?,
    @field:NotBlank val surname: String?,
    @field:DecimalMin("-5") @field:DecimalMax("5") val sportCategory: Double?
) {
    fun toDto(): UserDto {
        return UserDto(null, username, name, surname, sportCategory)
    }
}
