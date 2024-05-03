package mediaManager.auth.dtos.sendOtp

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SendOTPRequestDto(
    @field:Email
    @field:NotBlank
    val email: String = "",
)
