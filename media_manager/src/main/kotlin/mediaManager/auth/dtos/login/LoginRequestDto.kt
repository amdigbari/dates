package mediaManager.auth.dtos.login

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import mediaManager.validations.Password

class LoginRequestDto {
    @Email
    @NotBlank
    val email: String = ""

    @Password
    @NotBlank
    val password: String = ""
}
