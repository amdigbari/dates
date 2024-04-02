package mediaManager.auth.dtos.login

data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String,
)
