package mediaManager.auth.dtos.refreshToken

import jakarta.validation.constraints.NotNull

data class RefreshTokenResponseDto(
    @field:NotNull
    val accessToken: String,
    @field:NotNull
    val refreshToken: String,
)
