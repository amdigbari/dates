package mediaManager.auth.dtos.register

data class RegisterRequestDto(
    val email: String,
    val otp: String,
    val password: String,
)
