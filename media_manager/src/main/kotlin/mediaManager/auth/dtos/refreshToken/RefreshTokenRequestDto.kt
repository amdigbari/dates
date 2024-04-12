package mediaManager.auth.dtos.refreshToken

import jakarta.validation.constraints.NotEmpty

class RefreshTokenRequestDto {
    @NotEmpty
    val token: String = ""
}
