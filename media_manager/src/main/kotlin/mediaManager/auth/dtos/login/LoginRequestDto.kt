package mediaManager.auth.dtos.login

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import mediaManager.validations.Password

data class LoginRequestDto(
    @field:Email
    @field:NotBlank
    val email: String = "",
    @field:Password
    @field:NotBlank
    val password: String = "",
)
