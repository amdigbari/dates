package mediaManager.auth.dtos.sendOtp

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class SendOTPRequestDto {
    @Email
    @NotBlank
    val email: String = ""
}
