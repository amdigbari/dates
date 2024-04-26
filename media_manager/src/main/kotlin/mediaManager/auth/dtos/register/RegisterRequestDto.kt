package mediaManager.auth.dtos.register

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import mediaManager.validations.OTP
import mediaManager.validations.Password
import org.hibernate.validator.constraints.Length

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

    @NotBlank
    @Length(min = 5, max = 64)
    val fullName: String = ""

    @Length(min = 3, max = 32)
    val nickname: String? = null
}
