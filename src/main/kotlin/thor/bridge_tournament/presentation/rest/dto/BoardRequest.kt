package thor.bridge_tournament.presentation.rest.dto

import jakarta.validation.constraints.Positive

data class BoardRequest(@field:Positive val number: Int)
