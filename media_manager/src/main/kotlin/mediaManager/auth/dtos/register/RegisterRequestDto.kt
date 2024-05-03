package mediaManager.auth.dtos.register

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import mediaManager.validations.OTP
import mediaManager.validations.Password
import org.hibernate.validator.constraints.Length

data class RegisterRequestDto(
    @field:Email
    @field:NotBlank
    val email: String = "",
    @field:OTP
    @field:NotBlank
    val otp: String = "",
    @field:Password
    @field:NotBlank
    val password: String = "",
    @field:NotBlank
    @field:Length(min = 5, max = 64)
    val fullName: String = "",
    @field:Length(min = 3, max = 32)
    val nickname: String? = null,
)
