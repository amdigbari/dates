package mediaManager.auth.dtos.register

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import mediaManager.validations.OTP
import mediaManager.validations.Password

class RegisterRequestDto {
    @Email
    @NotBlank
    val email: String = ""

    @OTP
    @NotBlank
    val otp: String = ""

    @Password
    @NotBlank
    val password: String = ""
}
