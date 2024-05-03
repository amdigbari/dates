package mediaManager.auth.dtos.login

import jakarta.validation.constraints.NotNull

data class LoginResponseDto(
    @field:NotNull
    val accessToken: String = "",
    @field:NotNull
    val refreshToken: String = "",
)
