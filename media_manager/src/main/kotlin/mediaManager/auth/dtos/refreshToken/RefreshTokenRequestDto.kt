package mediaManager.auth.dtos.refreshToken

import jakarta.validation.constraints.NotEmpty

data class RefreshTokenRequestDto(
    @field:NotEmpty
    val token: String = "",
)
