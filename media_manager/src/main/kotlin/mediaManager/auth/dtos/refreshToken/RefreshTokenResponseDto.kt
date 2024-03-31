package mediaManager.auth.dtos.refreshToken

data class RefreshTokenResponseDto(
    val accessToken: String,
    val refreshToken: String,
)
